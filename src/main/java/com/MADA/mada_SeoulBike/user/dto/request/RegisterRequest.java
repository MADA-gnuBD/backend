package com.MADA.mada_SeoulBike.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    // 관리자가 관리자 등록시 사용하려면 role 필드도 추가 (옵션)
    private String role;
}
