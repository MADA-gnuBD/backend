package com.MADA.mada_SeoulBike.ddarungi.repository;

import com.MADA.mada_SeoulBike.ddarungi.entity.BikeInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeInventoryRepository extends JpaRepository<BikeInventory, Long> {
    Optional<BikeInventory> findFirstByStationIdOrderByTimestampDesc(String stationId);
}
