package com.MADA.mada_SeoulBike.ai.controller;

import com.MADA.mada_SeoulBike.ai.dto.request.RangePredictReq;
import com.MADA.mada_SeoulBike.ai.dto.request.SinglePredictReq;
import com.MADA.mada_SeoulBike.ai.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/predict")
    public Mono<ResponseEntity<Map<String, Object>>> predict(@Valid @RequestBody SinglePredictReq req) {
        return aiService.predict(req).map(ResponseEntity::ok);
    }

    @PostMapping("/range-predict")
    public Mono<ResponseEntity<Map<String, Object>>> rangePredict(@Valid @RequestBody RangePredictReq req) {
        return aiService.rangePredict(req).map(ResponseEntity::ok);
    }
}
