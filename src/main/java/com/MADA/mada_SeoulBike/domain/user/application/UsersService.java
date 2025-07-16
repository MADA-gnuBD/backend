package com.MADA.mada_SeoulBike.domain.user.application;

import com.MADA.mada_SeoulBike.domain.user.domain.UserRepository;
import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserLoginRequest;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserRequestDto;
import com.MADA.mada_SeoulBike.domain.user.dto.response.UserResponse;
import com.MADA.mada_SeoulBike.global.auth.application.AuthService;
import com.MADA.mada_SeoulBike.global.auth.dto.TokenResponse;
import com.MADA.mada_SeoulBike.global.exception.MyErrorCode;
import com.MADA.mada_SeoulBike.global.exception.MyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(UserRequestDto requestDto) {
        if (userRepository.findByUserId(requestDto.userId()).isPresent()) {
            throw new MyException(MyErrorCode.DUPLICATE_USER_ID);
        }

        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new MyException(MyErrorCode.DUPLICATE_EMAIL);
        }

        Users newUser = requestDto.toEntity(passwordEncoder);
        userRepository.save(newUser);
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse login(UserLoginRequest request) {
        Users user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new MyException(MyErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new MyException(MyErrorCode.PASSWORD_NOT_MATCH);
        }

        return authService.login(user);
    }

    /**
     * 내 정보 조회
     */
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

    /**
     * 회원정보 수정
     */
    @Transactional
    public void update(UserRequestDto requestDto) {
        Users user = authService.findCurrentUser();

        user.updateInfo(
                requestDto.userId(),
                passwordEncoder.encode(requestDto.password()),
                requestDto.userName(),
                requestDto.nickname(),
                requestDto.email()
        );
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void delete() {
        Users user = authService.findCurrentUser();
        user.markDeleted();
    }
}