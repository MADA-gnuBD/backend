package com.MADA.mada_SeoulBike.controller;

import com.MADA.mada_SeoulBike.entity.BikeInventory;
import com.MADA.mada_SeoulBike.repository.BikeInventoryRepository;
import com.MADA.mada_SeoulBike.service.BikeDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bike-inventory")
@CrossOrigin(origins = "http://localhost:3000") // CORS
public class BikeInventoryController {
    private final BikeInventoryRepository repo;
    private final BikeDataService bikeDataService;

    public BikeInventoryController(BikeInventoryRepository repo, BikeDataService bikeDataService) {
        this.repo = repo;
        this.bikeDataService = bikeDataService;
    }

    @GetMapping("/latest")
    public List<BikeInventory> getLatestInventory() throws Exception {
        List<BikeInventory> result = repo.findAll();
        if (result.isEmpty()) {
            bikeDataService.fetchAndSaveBikeData();
            result = repo.findAll();
        }
        return result;
    }

}

