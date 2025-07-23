package com.MADA.mada_SeoulBike.service;

import com.MADA.mada_SeoulBike.entity.BikeInventory;
import com.MADA.mada_SeoulBike.repository.BikeInventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

    // DB 전체 삭제 후 새로 채우기
    @Transactional
    public void resetAndFetchBikeData() {
        System.out.println("[서비스] DB 전체 삭제!");
        inventoryRepo.deleteAll();
        fetchAndSaveBikeData();
    }

    // 실시간 따릉이 전체 데이터 받아오기 (페이징 처리)
    @Transactional
    public void fetchAndSaveBikeData() {
        try {
            int start = 1;
            int batchSize = 1000;
            while (true) {
                int end = start + batchSize - 1;
                String url = "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/bikeList/" + start + "/" + end + "/";
                System.out.println("[서비스] 호출: " + url);

                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(url, String.class);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(result);
                JsonNode rows = root.path("rentBikeStatus").path("row");
                System.out.println("[서비스] 응답 row count: " + (rows == null ? 0 : rows.size()));

                if (rows == null || !rows.isArray() || rows.size() == 0) break;

                for (JsonNode node : rows) {
                    BikeInventory inventory = new BikeInventory();
                    inventory.setStationId(node.path("stationId").asText());
                    inventory.setStationName(node.path("stationName").asText());
                    inventory.setLatitude(node.path("stationLatitude").asDouble());
                    inventory.setLongitude(node.path("stationLongitude").asDouble());
                    inventory.setParkingBikeTotCnt(node.path("parkingBikeTotCnt").asInt());
                    inventory.setShared(node.path("shared").asInt());
                    inventory.setTimestamp(LocalDateTime.now());

                    inventoryRepo.save(inventory);
                    System.out.println("[서비스] 저장된 stationId: " + inventory.getStationId());
                }

                // 1000개보다 적게 내려오면 마지막 호출이므로 종료
                if (rows.size() < batchSize) break;

                start += batchSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(fixedRate = 300000) // 5분마다
    public void scheduledResetAndFetchBikeData() {
        System.out.println("[스케줄러] 5분마다 DB 전체 삭제 후 실시간 따릉이 데이터 갱신");
        resetAndFetchBikeData();
    }
}
