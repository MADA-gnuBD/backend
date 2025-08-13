package com.MADA.mada_SeoulBike.ai.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_prediction_log")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiPredictionLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AiJobType jobType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String requestJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String responseJson;

    @Column(length = 16)
    private String status; // SUCCESS / ERROR

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
