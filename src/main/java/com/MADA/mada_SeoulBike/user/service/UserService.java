package com.MADA.mada_SeoulBike.user.service;

import com.MADA.mada_SeoulBike.global.config.JwtProvider;
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

    //회원가입
    public User register(String email, String password, String name){
        //중복 이메일 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일 입니다.");
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .build();

        return userRepository.save(user);
    }

    //로그인: 성공시 User 반환, 실패시 예외
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    // 로그인 시 JWT 토큰 발급
    public String createJwtToken(User user) {
        return jwtProvider.generateToken(user.getId(), user.getEmail());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
