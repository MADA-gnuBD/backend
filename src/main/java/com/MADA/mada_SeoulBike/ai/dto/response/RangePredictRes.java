package com.MADA.mada_SeoulBike.ai.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public record RangePredictRes(
        double lat,
        double lng,
        int radius,
        int minutes,
        JsonNode result,     // Express에서 온 원본 결과(JSON) 그대로
        Instant requestedAt

) {
}
