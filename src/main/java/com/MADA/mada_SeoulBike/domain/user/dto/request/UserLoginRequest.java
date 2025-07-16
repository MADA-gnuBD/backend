package com.MADA.mada_SeoulBike.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequest {
    private String userId;
    private String password;
}
