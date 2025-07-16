package com.MADA.mada_SeoulBike.domain.user.presentation;

import com.MADA.mada_SeoulBike.domain.user.application.UsersService;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserLoginRequest;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserRequestDto;
import com.MADA.mada_SeoulBike.domain.user.dto.response.UserResponse;
import com.MADA.mada_SeoulBike.global.auth.dto.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {


    private final UsersService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest loginRequest,
                                               HttpServletResponse response) {
        TokenResponse tokenResponse = userService.login(loginRequest);

        Cookie cookie = new Cookie("access_token", tokenResponse.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간

        response.addCookie(cookie);
        return ResponseEntity.ok(tokenResponse);
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        UserResponse userResponse = userService.getMyInfo();
        return ResponseEntity.ok(userResponse);
    }

    // 회원 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<Void> updateProfile(@RequestBody UserRequestDto updateRequest) {
        userService.update(updateRequest);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        userService.delete();
        return ResponseEntity.ok().build();
    }

}