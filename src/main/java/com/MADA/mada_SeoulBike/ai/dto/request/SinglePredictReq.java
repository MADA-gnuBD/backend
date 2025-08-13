package com.MADA.mada_SeoulBike.ai.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinglePredictReq {
    @NotBlank
    private String stationId;   // 프론트에서 넘어오는 camelCase
    @NotNull
    private Integer minutes;
    @NotNull           // supply 필수로 받을게요(Express 계약 때문)
    private Integer supply;
}