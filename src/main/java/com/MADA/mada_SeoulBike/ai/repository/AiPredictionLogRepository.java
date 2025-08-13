package com.MADA.mada_SeoulBike.ai.repository;

import com.MADA.mada_SeoulBike.ai.entitiy.AiPredictionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiPredictionLogRepository extends JpaRepository<AiPredictionLog, UUID> {
}