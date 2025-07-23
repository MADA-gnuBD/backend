package com.MADA.mada_SeoulBike.controller;

import com.MADA.mada_SeoulBike.entity.BikeInventory;
import com.MADA.mada_SeoulBike.repository.BikeInventoryRepository;
import com.MADA.mada_SeoulBike.service.BikeDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bike-inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class BikeInventoryController {
    private final BikeInventoryRepository repo;
    private final BikeDataService bikeDataService;

    public BikeInventoryController(BikeInventoryRepository repo, BikeDataService bikeDataService) {
        this.repo = repo;
        this.bikeDataService = bikeDataService;
    }

    @GetMapping("/latest")
    public List<BikeInventory> getLatestInventory() {
        System.out.println("[컨트롤러] getLatestInventory() 호출됨");
        List<BikeInventory> result = repo.findAll();
        if (result.isEmpty()) {
            System.out.println("[컨트롤러] DB 비어있음 → fetchAndSaveBikeData() 실행");
            bikeDataService.fetchAndSaveBikeData();
            result = repo.findAll();
        } else {
            System.out.println("[컨트롤러] DB에 이미 데이터 있음: " + result.size() + "개");
        }
        return result;
    }

    @PostMapping("/reset")
    public String resetAndRefetch() {
        System.out.println("[컨트롤러] DB 초기화 요청 → resetAndFetchBikeData() 실행");
        bikeDataService.resetAndFetchBikeData();
        return "DB 초기화 및 따릉이 API로 새로 채움 완료!";
    }
}
