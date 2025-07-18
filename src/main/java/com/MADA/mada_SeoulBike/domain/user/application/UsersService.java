package com.MADA.mada_SeoulBike.domain.user.application;

import com.MADA.mada_SeoulBike.domain.user.domain.UserRepository;
import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserLoginRequest;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserRequestDto;
import com.MADA.mada_SeoulBike.domain.user.dto.response.UserResponse;
import com.MADA.mada_SeoulBike.global.auth.application.AuthService;
import com.MADA.mada_SeoulBike.global.auth.domain.RefreshToken;
import com.MADA.mada_SeoulBike.global.auth.domain.RefreshTokenRepository;
import com.MADA.mada_SeoulBike.global.auth.dto.TokenResponse;
import com.MADA.mada_SeoulBike.global.auth.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(UserRequestDto requestDto) {
        if (userRepository.findByUserId(requestDto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID입니다.");
        }
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        Users user = requestDto.toEntity(passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse login(UserLoginRequest request) {
        Users user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.generateToken(Duration.ofHours(1), user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        // RefreshToken 저장 (이미 있으면 업데이트)
        RefreshToken saved = refreshTokenRepository.findByUserId(user.getId())
                .map(rt -> rt.update(refreshToken))
                .orElse(new RefreshToken(user.getId(), refreshToken));

        refreshTokenRepository.save(saved);

        return TokenResponse.of(accessToken, refreshToken);

    }

    @Transactional
    public Users signupSocial(String email, String provider, String providerId) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Users user = Users.builder()
                            .userId(provider + "_" + providerId)
                            .password("") // social login은 비밀번호 null 또는 별도 처리
                            .userName("소셜사용자")
                            .email(email)
                            .provider(provider)
                            .providerId(providerId)
                            .deleted(false)
                            .build();
                    return userRepository.save(user);
                });
    }

    public UserResponse getMyInfo() {
        Users user = authService.findCurrentUser();
        return UserResponse.of(
                user.getId(),
                user.getUserId(),
                user.getUserName(),
                user.getNickname(),
                user.getEmail()
        );
    }

    @Transactional
    public void update(UserRequestDto requestDto) {
        Users user = authService.findCurrentUser();
        user.updateInfo(
                requestDto.getUserId(),
                requestDto.getPassword() != null ? passwordEncoder.encode(requestDto.getPassword()) : null,
                requestDto.getUserName(),
                requestDto.getNickname(),
                requestDto.getEmail()
        );
    }

    @Transactional
    public void delete() {
        Users user = authService.findCurrentUser();
        user.markDeleted();
    }
}
