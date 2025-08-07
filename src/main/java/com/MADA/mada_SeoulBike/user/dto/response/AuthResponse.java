package com.MADA.mada_SeoulBike.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String accessToken;
    private String refreshToken;
}
