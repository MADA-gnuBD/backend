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
                String role  = jwtProvider.getRoleFromToken(token); // null 가능

                // 🔴 핵심: 컨트롤러/서비스에서 꺼내 쓰도록 request attribute로 심기
                req.setAttribute("userEmail", email);
                if (role != null && !role.isBlank()) {
                    req.setAttribute("userRole", role);
                }

                var authorities = (role == null || role.isBlank())
                        ? Collections.<SimpleGrantedAuthority>emptyList()
                        : List.of(new SimpleGrantedAuthority("ROLE_" + role));

                var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
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