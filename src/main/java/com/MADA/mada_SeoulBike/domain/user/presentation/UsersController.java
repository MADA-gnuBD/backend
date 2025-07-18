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

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest loginRequest,
                                               HttpServletResponse response) {
        TokenResponse tokenResponse = userService.login(loginRequest);

        Cookie cookie = new Cookie("access_token", tokenResponse.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateProfile(@RequestBody UserRequestDto updateRequest) {
        userService.update(updateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        userService.delete();
        return ResponseEntity.ok().build();
    }
}
