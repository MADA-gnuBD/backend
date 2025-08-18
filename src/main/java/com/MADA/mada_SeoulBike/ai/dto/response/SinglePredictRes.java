package com.MADA.mada_SeoulBike.ai.dto.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record SinglePredictRes(
        String stationId,
        int minutes,
        JsonNode result,
        Instant requestedAt
) {
}
