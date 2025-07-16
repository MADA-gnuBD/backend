package com.MADA.mada_SeoulBike.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpRequest {
    private String userId;
    private String password;
    private String userName;
    private String email;
    private String nickname;
}
