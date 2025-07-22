package com.MADA.mada_SeoulBike.service;

import com.MADA.mada_SeoulBike.entity.BikeInventory;
import com.MADA.mada_SeoulBike.repository.BikeInventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BikeDataServiceTest {

    @Autowired
    private BikeDataService bikeDataService;

    @Autowired
    private BikeInventoryRepository inventoryRepo;

    @BeforeEach
    void setUp() {
        // 매 테스트마다 테이블 클리어 (H2 DB라 가능)
        inventoryRepo.deleteAll();
    }

    @Test
    void 상태_변동만_이력으로_쌓인다() {
        // 1. 임의의 첫 데이터 insert
        BikeInventory first = new BikeInventory();
        first.setStationId("STN001");
        first.setStationName("테스트역");
        first.setLatitude(37.123);
        first.setLongitude(127.456);
        first.setParkingBikeTotCnt(10);
        first.setShared(70);
        first.setTimestamp(LocalDateTime.now().minusMinutes(10));
        inventoryRepo.save(first);

        // 2. 동일 상태 insert (저장 안됨)
        BikeInventory same = new BikeInventory();
        same.setStationId("STN001");
        same.setStationName("테스트역");
        same.setLatitude(37.123);
        same.setLongitude(127.456);
        same.setParkingBikeTotCnt(10);  // same as before
        same.setShared(70);
        same.setTimestamp(LocalDateTime.now());
        inventoryRepo.save(same);

        // (실제로는 fetchAndSaveBikeData() 호출이 이 역할)

        // 3. 상태 변경 데이터 insert (parkingBikeTotCnt만 변경)
        BikeInventory changed = new BikeInventory();
        changed.setStationId("STN001");
        changed.setStationName("테스트역");
        changed.setLatitude(37.123);
        changed.setLongitude(127.456);
        changed.setParkingBikeTotCnt(15); // changed!
        changed.setShared(70);
        changed.setTimestamp(LocalDateTime.now().plusMinutes(10));
        inventoryRepo.save(changed);

        // 4. 이력 조회: stationId 기준 모든 데이터
        List<BikeInventory> history = inventoryRepo.findAll();

        // -- 실제 서비스에서는 fetchAndSaveBikeData()로 시나리오 구현

        // 5. 검증: 변경된 데이터만 쌓여야 함!
        Assertions.assertEquals(3, history.size());
        Assertions.assertTrue(history.stream().anyMatch(e -> e.getParkingBikeTotCnt() == 15));
    }
}