package com.MADA.mada_SeoulBike.service;

import com.MADA.mada_SeoulBike.entity.BikeInventory;
import com.MADA.mada_SeoulBike.repository.BikeInventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class BikeDataService {

    @Value("${ddarungi.api.key}")
    private String apiKey;

    private final BikeInventoryRepository inventoryRepo;

    public BikeDataService(BikeInventoryRepository inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional
    public void fetchAndSaveBikeData() {
        try {
            String url = "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/bikeList/1/1000/";
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(result);
            JsonNode rows = root.path("rentBikeStatus").path("row");

            for (JsonNode node : rows) {
                String stationId = node.path("stationId").asText();
                String stationName = node.path("stationName").asText();
                Double latitude = node.path("stationLatitude").asDouble();
                Double longitude = node.path("stationLongitude").asDouble();
                Integer parkingBikeTotCnt = node.path("parkingBikeTotCnt").asInt();
                Integer shared = node.path("shared").asInt();

                // Upsert: 존재하면 update, 없으면 insert
                BikeInventory inventory = inventoryRepo.findFirstByStationIdOrderByTimestampDesc(stationId)
                        .orElse(new BikeInventory());

                inventory.setStationId(stationId);
                inventory.setStationName(stationName);
                inventory.setLatitude(latitude);
                inventory.setLongitude(longitude);
                inventory.setParkingBikeTotCnt(parkingBikeTotCnt);
                inventory.setShared(shared);
                inventory.setTimestamp(LocalDateTime.now());

                inventoryRepo.save(inventory);
                System.out.println("저장 또는 업데이트된 stationId: " + inventory.getStationId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

