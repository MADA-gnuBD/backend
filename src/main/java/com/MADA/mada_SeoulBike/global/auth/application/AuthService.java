package com.MADA.mada_SeoulBike.global.auth.application;

import com.MADA.mada_SeoulBike.domain.user.domain.UserRepository;
import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import com.MADA.mada_SeoulBike.global.auth.domain.RefreshToken;
import com.MADA.mada_SeoulBike.global.auth.domain.RefreshTokenRepository;
import com.MADA.mada_SeoulBike.global.auth.dto.TokenResponse;
import com.MADA.mada_SeoulBike.global.auth.jwt.TokenProvider;
import com.MADA.mada_SeoulBike.global.exception.MyErrorCode;
import com.MADA.mada_SeoulBike.global.exception.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private static final Duration ACCESS_TOKEN_EXP = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_EXP = Duration.ofDays(14);

    public Users findCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetails) principal).getUsername();
            log.debug("Authenticated username: {}", username);

            return userRepository.findByUserId(username)
                    .orElseThrow(() -> new MyException(MyErrorCode.USER_NOT_FOUND));
        } catch (Exception e) {
            throw new MyException(MyErrorCode.USER_NOT_FOUND);
        }
    }

    @Transactional
    public TokenResponse login(Users user) {
        // 기존 리프레시 토큰 제거
        refreshTokenRepository.deleteByUserId(user.getId());

        // 토큰 생성
        String accessToken = tokenProvider.generateToken(ACCESS_TOKEN_EXP, user);
        String refreshToken = tokenProvider.generateToken(REFRESH_TOKEN_EXP, user);

        // 리프레시 토큰 저장
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .refreshToken(refreshToken)
                        .build()
        );

        return TokenResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        // 1. 토큰 유효성 확인
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new MyException(MyErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. DB에 있는 리프레시 토큰 확인
        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MyException(MyErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 3. 유저 조회
        Users user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new MyException(MyErrorCode.USER_NOT_FOUND));

        // 4. 새 토큰 발급 및 저장
        String newAccessToken = tokenProvider.generateToken(ACCESS_TOKEN_EXP, user);
        String newRefreshToken = tokenProvider.generateToken(REFRESH_TOKEN_EXP, user);

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .refreshToken(newRefreshToken)
                        .build()
        );

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }
}
