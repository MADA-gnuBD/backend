package com.MADA.mada_SeoulBike.domain.user.dto.request;

import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String userId;
    private String password;
    private String userName;
    private String email;
    private String nickname;

    @Builder
    public UserRequestDto(String userId, String password, String userName, String email, String nickname) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.nickname = nickname;
    }

    public Users toEntity(BCryptPasswordEncoder encoder) {
        return Users.builder()
                .userId(userId)
                .password(encoder.encode(password))
                .userName(userName != null ? userName : "소셜사용자")
                .nickname(nickname)
                .email(email)
                .deleted(false)
                .build();
    }
}
