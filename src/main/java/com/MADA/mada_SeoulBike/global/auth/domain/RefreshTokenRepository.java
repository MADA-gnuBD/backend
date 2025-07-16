package com.MADA.mada_SeoulBike.global.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
