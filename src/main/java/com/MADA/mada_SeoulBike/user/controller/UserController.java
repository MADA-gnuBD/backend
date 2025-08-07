package com.MADA.mada_SeoulBike.user.controller;

import com.MADA.mada_SeoulBike.user.dto.request.LoginRequest;
import com.MADA.mada_SeoulBike.user.dto.request.RegisterRequest;
import com.MADA.mada_SeoulBike.user.dto.request.UserUpdateRequest;
import com.MADA.mada_SeoulBike.user.dto.response.AuthResponse;
import com.MADA.mada_SeoulBike.user.dto.response.UserResponse;
import com.MADA.mada_SeoulBike.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody RegisterRequest req) {
        try {
            System.out.println("🔍 Signup request: " + req.getEmail());

            AuthResponse authResponse = userService.register(req);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", authResponse.getAccessToken());
            response.put("user", Map.of(
                    "id", authResponse.getId(),
                    "email", authResponse.getEmail(),
                    "name", authResponse.getName(),
                    "role", authResponse.getRole().toLowerCase()
            ));

            System.out.println("✅ Signup success: " + authResponse.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Signup error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req) {
        try {
            System.out.println("🔍 Login request received: " + req.getEmail());

            AuthResponse authResponse = userService.login(req);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", authResponse.getAccessToken());
            response.put("user", Map.of(
                    "id", authResponse.getId(),
                    "email", authResponse.getEmail(),
                    "name", authResponse.getName(),
                    "role", authResponse.getRole().toLowerCase()
            ));

            System.out.println("✅ Login success: " + authResponse.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 🆕 토큰 검증용 엔드포인트 (프론트엔드 /api/auth/verify에서 사용)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        try {
            System.out.println("===== [Controller] GET /api/users/me 진입 =====");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + auth);
            System.out.println("Principal: " + auth.getPrincipal());

            String email = (String) auth.getPrincipal();
            System.out.println("Email from token: " + email);

            UserResponse user = userService.getUserInfo(email);
            System.out.println("✅ User info retrieved: " + user.getEmail());

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.err.println("❌ Get user info error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@RequestBody UserUpdateRequest req) {
        try {
            System.out.println("===== [Controller] PUT /api/users/me 진입 =====");
            System.out.println("Update request: " + req);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + auth);

            String email = (String) auth.getPrincipal();
            System.out.println("Email from token: " + email);

            UserResponse updated = userService.updateUser(email, req);
            System.out.println("✅ User updated: " + updated.getEmail());

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            System.err.println("❌ Update user error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body((UserResponse) errorResponse);
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteAccount() {
        try {
            System.out.println("===== [Controller] DELETE /api/users/me 진입 =====");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + auth);

            String email = (String) auth.getPrincipal();
            System.out.println("Email from token: " + email);

            userService.deleteUser(email);
            System.out.println("✅ User deleted: " + email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "계정이 성공적으로 삭제되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Delete user error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
/*
    // 🆕 헬스체크 엔드포인트 (프론트엔드 /api/health-check에서 사용)
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        System.out.println("🔍 Health check requested");

        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Spring Boot 서버가 정상 작동 중입니다.");
        status.put("timestamp", LocalDateTime.now().toString());

        System.out.println("✅ Health check response sent");
        return ResponseEntity.ok(status);
    }
*/
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        try {
            System.out.println("🔍 Refresh token request");

            String refreshToken = body.get("refreshToken");
            AuthResponse res = userService.refreshToken(refreshToken);

            System.out.println("✅ Token refreshed for: " + res.getEmail());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println("❌ Refresh token error: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
