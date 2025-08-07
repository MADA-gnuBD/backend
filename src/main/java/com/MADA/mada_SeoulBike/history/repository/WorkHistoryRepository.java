package com.MADA.mada_SeoulBike.history.repository;

import com.MADA.mada_SeoulBike.history.entity.WorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;

public interface WorkHistoryRepository extends JpaRepository<WorkHistory, String> {

    // 전체 유저 작업 내역
    List<WorkHistory> findByUserId(String userId);

    // 오늘 작업 내역 - PostgreSQL 기준 날짜만 비교 (LocalDateTime → date로 캐스팅)
    @Query("SELECT w FROM WorkHistory w WHERE w.userId = :userId AND CAST(w.completedAt AS date) = CURRENT_DATE")
    List<WorkHistory> findTodayByUserId(@Param("userId") String userId);

    // (추가로 날짜 범위도 가능)
    List<WorkHistory> findByUserIdAndCompletedAtBetween(String userId, LocalDateTime start, LocalDateTime end);
}
