package com.MADA.mada_SeoulBike.community.dto.request;

import lombok.Data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequest {
    private String category;
    private String title;
    private String content;
}
