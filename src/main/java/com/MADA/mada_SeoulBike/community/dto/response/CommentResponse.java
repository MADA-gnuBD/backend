package com.MADA.mada_SeoulBike.community.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String author;
    private String authorId;
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
