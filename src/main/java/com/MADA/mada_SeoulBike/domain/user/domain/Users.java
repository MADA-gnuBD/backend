package com.MADA.mada_SeoulBike.domain.user.domain;


import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId; //사용자 아이디

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 500)
    private String userName; //실제 이름

    @Column(nullable = true, length = 500)
    private String nickname; //닉네임

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String provider; //소셜 로그인 제공자 (google, kakao)
    private String providerId; //소셜 로그인 id

    private boolean deleted = false; //탈퇴여부

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateInfo(String userId, String password,
                           String userName, String nickname, String email) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.nickname = nickname;
        this.email = email;
    }

    public void markDeleted() {
        this.deleted = true;
    }

}
