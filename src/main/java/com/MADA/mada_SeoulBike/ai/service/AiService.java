package com.MADA.mada_SeoulBike.ai.service;

import com.MADA.mada_SeoulBike.ai.dto.request.RangePredictReq;
import com.MADA.mada_SeoulBike.ai.dto.request.SinglePredictReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${ai.base-url}")
    private String aiBaseUrl; // 예: http://express:4000 (도커) / http://localhost:4000 (로컬)

    private final WebClient.Builder webClientBuilder;

    // 컨트롤러가 DTO로 호출 -> 오버로드 메서드
    public Mono<Map<String, Object>> predict(SinglePredictReq req) {
        return predict(req.getStationId(), req.getMinutes());
    }

    public Mono<Map<String, Object>> rangePredict(RangePredictReq req) {
        Map<String, Object> body = new HashMap<>();
        if (req.getLat() != null) body.put("lat", req.getLat());
        if (req.getLng() != null) body.put("lng", req.getLng());
        if (req.getRadius() != null) body.put("radius", req.getRadius());
        if (req.getMinutes() != null) body.put("minutes", req.getMinutes());

        WebClient client = webClientBuilder.baseUrl(aiBaseUrl).build();
        return client.post()
                .uri("/range-predict")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // 내부 구현: Express 계약에 맞춰 호출
    public Mono<Map<String, Object>> predict(String stationId, Integer minutes) {
        // Express /predict 계약: station_id + supply 필수
        // stationId가 "ST-" 프리픽스 없으면 붙여서 전달
        String stationIdForExpress = (stationId != null && stationId.startsWith("ST-"))
                ? stationId
                : "ST-" + stationId;

        Map<String, Object> body = new HashMap<>();
        body.put("station_id", stationIdForExpress);
        body.put("supply", 0);                  // 계약 충족용 필드 (액션 계산에 필요)
        if (minutes != null) body.put("minutes", minutes);

        WebClient client = webClientBuilder.baseUrl(aiBaseUrl).build();
        return client.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}
