package com.MADA.mada_SeoulBike.user.service;

import com.MADA.mada_SeoulBike.global.config.JwtProvider;
import com.MADA.mada_SeoulBike.user.dto.request.LoginRequest;
import com.MADA.mada_SeoulBike.user.dto.request.RegisterRequest;
import com.MADA.mada_SeoulBike.user.dto.request.UserUpdateRequest;
import com.MADA.mada_SeoulBike.user.dto.response.AuthResponse;
import com.MADA.mada_SeoulBike.user.dto.response.UserResponse;
import com.MADA.mada_SeoulBike.user.entity.User;
import com.MADA.mada_SeoulBike.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role("USER")
                .build();

        String accessToken = jwtProvider.generateToken(user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole()) // 혹은 .role(user.getRole().toLowerCase()) 필요하면!
                .build();
    }

    // 로그인
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        String accessToken = jwtProvider.generateToken(user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole()) // 소문자 변환 필요시 .toLowerCase()
                .build();
    }

    // RefreshToken 재발급
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("refreshToken이 유효하지 않습니다.");
        }
        String email = jwtProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("refreshToken이 일치하지 않습니다.");
        }

        String newAccessToken = jwtProvider.generateToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    // 내 정보 반환
    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().toLowerCase()) // 프론트 기대 소문자
                .build();
    }

    // 내 정보 수정
    public UserResponse updateUser(String email, UserUpdateRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 이름 변경
        if (req.getName() != null && !req.getName().isBlank()) {
            user.setName(req.getName());
        }
        // 비밀번호 변경(현재 비밀번호 확인)
        if (req.getCurrentPassword() != null && req.getNewPassword() != null) {
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
            }
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().toLowerCase())
                .build();
    }

    // 계정 삭제
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

    // 기타 util
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
