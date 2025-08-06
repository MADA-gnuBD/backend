package com.MADA.mada_SeoulBike.history.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateWorkHistoryRequest {
    private String userId;
    private String stationName;
    private String stationId;
    private String action;
    private String notes;
    private LocalDateTime completedAt;
}
