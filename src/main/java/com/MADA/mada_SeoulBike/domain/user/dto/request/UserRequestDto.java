package com.MADA.mada_SeoulBike.domain.user.dto.request;

import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
public record UserRequestDto (
        String userId,
        String password,
        String userName,
        String email,
        String nickname) {
    public Users toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Users.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(password))
                .userName(userName)
                .email(email)
                .nickname(nickname)
                .build();

    }

}
