package com.MADA.mada_SeoulBike.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String userId;
    private String userName;
    private String nickname;
    private String email;

    public static UserResponse of(Long id, String userId, String userName, String nickname, String email) {
        return UserResponse.builder()
                .id(id)
                .userId(userId)
                .userName(userName)
                .nickname(nickname)
                .email(email)
                .build();
    }
}
