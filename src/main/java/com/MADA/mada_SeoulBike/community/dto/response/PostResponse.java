package com.MADA.mada_SeoulBike.community.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String category;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private String authorRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likes;
    private boolean isNotice;
}
