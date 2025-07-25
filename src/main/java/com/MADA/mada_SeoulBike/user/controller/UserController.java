package com.MADA.mada_SeoulBike.user.controller;

import com.MADA.mada_SeoulBike.global.config.JwtProvider;
import com.MADA.mada_SeoulBike.user.dto.request.LoginRequest;
import com.MADA.mada_SeoulBike.user.dto.request.RegisterRequest;
import com.MADA.mada_SeoulBike.user.dto.response.LoginResponse;
import com.MADA.mada_SeoulBike.user.dto.response.RegisterResponse;
import com.MADA.mada_SeoulBike.user.entity.User;
import com.MADA.mada_SeoulBike.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider; // 직접 주입

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request.getEmail(), request.getPassword(), request.getName());
            return ResponseEntity.ok(new RegisterResponse(user.getId(), user.getEmail(), user.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            String token = userService.createJwtToken(user);
            return ResponseEntity.ok(new LoginResponse(user.getId(), user.getEmail(), user.getName(), token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean valid = jwtProvider.validateToken(token);
        if (!valid) return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        Long userId = jwtProvider.getUserId(token);
        String email = jwtProvider.getEmail(token);
        // name도 반환하고 싶으면 userService로 user 찾아서 반환
        User user = userService.findByEmail(email); // 메서드 추가 필요
        return ResponseEntity.ok(new LoginResponse(userId, email, user.getName(), token));
    }
}
