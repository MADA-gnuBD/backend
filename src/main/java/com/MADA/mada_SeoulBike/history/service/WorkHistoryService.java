package com.MADA.mada_SeoulBike.history.service;

import com.MADA.mada_SeoulBike.history.entity.WorkHistory;
import com.MADA.mada_SeoulBike.history.repository.WorkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
@RequiredArgsConstructor
public class WorkHistoryService {

    private final WorkHistoryRepository workHistoryRepository;

    // 전체 내역 조회
    public List<WorkHistory> getWorkHistory(String userId) {
        return workHistoryRepository.findByUserId(userId);
    }

    // 오늘 내역 조회 (PostgreSQL 기준 JPQL)
    public List<WorkHistory> getTodayWorkHistory(String userId) {
        return workHistoryRepository.findTodayByUserId(userId);
    }

    // (MariaDB/MySQL/호환용: 범위로 today 뽑고 싶으면 이렇게도 가능)
    public List<WorkHistory> getTodayWorkHistoryByRange(String userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return workHistoryRepository.findByUserIdAndCompletedAtBetween(userId, start, end);
    }

    // 작업 등록
    @Transactional
    public WorkHistory createWorkHistory(WorkHistory workHistory) {
        workHistory.setId(UUID.randomUUID().toString());
        workHistory.setCreatedAt(LocalDateTime.now());
        return workHistoryRepository.save(workHistory);
    }

    // 삭제
    @Transactional
    public void deleteWorkHistory(String id) {
        workHistoryRepository.deleteById(id);
    }
}

