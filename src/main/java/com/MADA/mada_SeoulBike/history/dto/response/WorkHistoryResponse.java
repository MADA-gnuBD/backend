package com.MADA.mada_SeoulBike.history.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkHistoryResponse {
    private String id;
    private String userId;
    private String stationName;
    private String stationId;
    private String action;
    private String notes;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}
