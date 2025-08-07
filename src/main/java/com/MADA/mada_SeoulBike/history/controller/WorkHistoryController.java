package com.MADA.mada_SeoulBike.history.controller;


import com.MADA.mada_SeoulBike.history.dto.request.CreateWorkHistoryRequest;
import com.MADA.mada_SeoulBike.history.dto.response.WorkHistoryResponse;
import com.MADA.mada_SeoulBike.history.entity.WorkHistory;
import com.MADA.mada_SeoulBike.history.service.WorkHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/work-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkHistoryController {

    private final WorkHistoryService workHistoryService;

    @GetMapping
    public ResponseEntity<List<WorkHistoryResponse>> getWorkHistory(
            @RequestParam(required = false) String userId,
            Authentication authentication) {

        System.out.println("🔍 [WorkHistoryController] GET /api/work-history 진입");
        System.out.println("🔍 [WorkHistoryController] Authentication: " + authentication);
        System.out.println("🔍 [WorkHistoryController] Authentication.getName(): " + (authentication != null ? authentication.getName() : "null"));

        if (authentication == null) {
            System.err.println("❌ [WorkHistoryController] Authentication is null!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();
        String targetUserId = userId != null ? userId : userEmail;

        System.out.println("🔍 [WorkHistoryController] targetUserId: " + targetUserId);

        List<WorkHistory> workHistoryList = workHistoryService.getWorkHistory(targetUserId);
        System.out.println("🔍 [WorkHistoryController] Found work history count: " + workHistoryList.size());

        for (WorkHistory wh : workHistoryList) {
            System.out.println("🔍 [WorkHistoryController] WorkHistory: " + wh.getId() + " - " + wh.getAction() + " - " + wh.getStationName());
        }

        // WorkHistory 엔티티를 WorkHistoryResponse DTO로 변환
        List<WorkHistoryResponse> responses = workHistoryList.stream()
                .map(workHistory -> WorkHistoryResponse.builder()
                        .id(workHistory.getId())
                        .userId(workHistory.getUserId())
                        .stationName(workHistory.getStationName())
                        .stationId(workHistory.getStationId())
                        .action(workHistory.getAction())
                        .notes(workHistory.getNotes())
                        .completedAt(workHistory.getCompletedAt())
                        .createdAt(workHistory.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        System.out.println("✅ [WorkHistoryController] 작업 이력 조회 완료: " + responses.size() + "개");

        return ResponseEntity.ok(responses);
    }

    // 작업 등록
    @PostMapping
    public ResponseEntity<WorkHistoryResponse> createWorkHistory(
            @RequestBody CreateWorkHistoryRequest req,
            Authentication authentication) {

        System.out.println("🔍 [WorkHistoryController] POST /api/work-history 진입");
        System.out.println("🔍 [WorkHistoryController] Request body: " + req);
        System.out.println("🔍 [WorkHistoryController] Authentication: " + authentication);

        String userEmail = authentication.getName();

        // CreateWorkHistoryRequest를 WorkHistory 엔티티로 변환
        WorkHistory workHistory = WorkHistory.builder()
                .id(UUID.randomUUID().toString())
                .userId(userEmail)  // 인증된 사용자 이메일 사용
                .stationName(req.getStationName())
                .stationId(req.getStationId())
                .action(req.getAction())
                .notes(req.getNotes())
                .completedAt(req.getCompletedAt() != null ? req.getCompletedAt() : LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        System.out.println("🔍 [WorkHistoryController] Created WorkHistory: " + workHistory);

        WorkHistory created = workHistoryService.createWorkHistory(workHistory);

        // WorkHistory 엔티티를 WorkHistoryResponse DTO로 변환
        WorkHistoryResponse response = WorkHistoryResponse.builder()
                .id(created.getId())
                .userId(created.getUserId())
                .stationName(created.getStationName())
                .stationId(created.getStationId())
                .action(created.getAction())
                .notes(created.getNotes())
                .completedAt(created.getCompletedAt())
                .createdAt(created.getCreatedAt())
                .build();

        System.out.println("✅ [WorkHistoryController] 작업 이력 생성 완료: " + response);

        return ResponseEntity.ok(response);
    }

    // 작업 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkHistory(@PathVariable String id) {
        System.out.println("🔍 [WorkHistoryController] DELETE /api/work-history/" + id + " 진입");

        workHistoryService.deleteWorkHistory(id);
        System.out.println("✅ [WorkHistoryController] 작업 이력 삭제 완료");

        return ResponseEntity.ok().build();
    }
}