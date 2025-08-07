package com.MADA.mada_SeoulBike.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String token; // 로그인, 회원가입시 반환 / 조회에서는 null

}
