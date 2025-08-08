package com.MADA.mada_SeoulBike.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // 인증 없이 통과시킬 URI
    private static final Set<String> PERMIT_URI = Set.of(
            "/api/users/login",
            "/api/users/signup",
            "/api/users/refresh",
            "/bike-inventory/latest"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("🔍 [JwtFilter] " + method + " " + uri);

        // 1. 인증 없이 통과시켜야 하는 URI 예외 먼저 체크
        if (PERMIT_URI.contains(uri)) {
            System.out.println("✅ [JwtFilter] PERMIT ALL URI, passing through");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Authorization 헤더 가져오기
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader)) {
            // 2-1. Bearer 토큰(JWT)인 경우
            if (authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                System.out.println("🔍 [JwtFilter] Extracted JWT: " + (jwt.length() > 20 ? jwt.substring(0, 20) + "..." : jwt));
                if (jwtProvider.validateToken(jwt)) {
                    String email = jwtProvider.getEmailFromToken(jwt);
                    System.out.println("✅ [JwtFilter] Valid JWT for email: " + email);
                    request.setAttribute("userEmail", email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                    return;
                } else {
                    System.err.println("❌ [JwtFilter] Invalid JWT");
                    sendUnauthorized(response, "Invalid JWT");
                    return;
                }
            }
            // 2-2. 이메일(plain email) 인증 방식인 경우
            else if (authHeader.contains("@")) {
                String email = authHeader.trim();
                System.out.println("✅ [JwtFilter] Email Auth detected! email: " + email);
                request.setAttribute("userEmail", email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
                return;
            }
            // 2-3. 그 외 형식
            else {
                System.err.println("❌ [JwtFilter] Unsupported Authorization header: " + authHeader);
                sendUnauthorized(response, "Unsupported Authorization header");
                return;
            }
        }

        // 3. Authorization 헤더 자체가 없는 경우
        System.err.println("❌ [JwtFilter] Authorization header missing");
        sendUnauthorized(response, "Authorization header missing");
    }

    // 401 에러 응답
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"success\":false, \"error\":\"" + message + "\"}");
    }
}
