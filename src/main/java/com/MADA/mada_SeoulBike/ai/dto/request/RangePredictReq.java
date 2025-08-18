package com.MADA.mada_SeoulBike.ai.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RangePredictReq {
    @NotNull private Double lat;
    @NotNull private Double lng;
    @NotNull private Integer radius;
    @NotNull private Integer minutes;
}
