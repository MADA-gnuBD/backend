package com.MADA.mada_SeoulBike.community.dto.request;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String content;
    // author/authorId는 백엔드에서 자동으로 주입
}
