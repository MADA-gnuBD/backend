package com.MADA.mada_SeoulBike.global.auth.filter;

import com.MADA.mada_SeoulBike.global.auth.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String COOKIE_NAME = "access_token";


    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getAccessToken(request);
        String path = request.getRequestURI();

        // ✅ 정적 리소스나 푸시 알림 관련 경로는 인증 검사 없이 통과
        if (path.equals("/firebase-messaging-sw.js") ||
                path.equals("/service-worker.js") ||
                path.equals("/api/notifications/register-token") ||
                path.startsWith("/static/") ||
                path.endsWith(".js") ||
                path.endsWith(".css") ||
                path.endsWith(".html") ||
                path.endsWith(".ico")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}