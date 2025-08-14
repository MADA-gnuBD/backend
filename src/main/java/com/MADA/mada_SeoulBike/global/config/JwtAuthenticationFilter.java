package com.MADA.mada_SeoulBike.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /** permitAll 프리픽스(하위 전체 스킵) */
    private static final List<String> SKIP_PREFIXES = List.of(
            "/api/ai/",
            "/bike-inventory/",
            "/api/users/login",
            "/api/users/signup",
            "/api/users/refresh"
    );

    private boolean shouldSkip(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // CORS preflight
        String uri = request.getRequestURI();
        for (String p : SKIP_PREFIXES) {
            if (uri.startsWith(p)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        if (shouldSkip(req)) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res); // 보호된 경로는 이후 Security에서 401/403
            return;
        }

        String token = header.substring(7);

        try {
            if (jwtProvider.validateToken(token)) {
                String email = jwtProvider.getEmailFromToken(token);
                String rawRole  = jwtProvider.getRoleFromToken(token); // null 가능

                // ✅ 정규화: 없으면 USER, 대문자, ROLE_ 제거
                String norm = (rawRole == null ? "USER" : rawRole)
                        .toUpperCase(Locale.ROOT)
                        .replaceFirst("^ROLE_", "");

                // 컨트롤러/서비스에서 꺼내 쓰도록 저장
                req.setAttribute("userEmail", email);
                req.setAttribute("userRole", norm); // ex) ADMIN

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + norm)); // ex) ROLE_ADMIN
                var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

                // (선택) 디버그
                System.out.println("[JWT] email=" + email + ", role=" + norm + ", auth=" + authorities);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            // 그냥 통과 → permitAll 경로 막지 않고, 보호된 곳은 나중에 401/403
        }

        chain.doFilter(req, res);
    }
}