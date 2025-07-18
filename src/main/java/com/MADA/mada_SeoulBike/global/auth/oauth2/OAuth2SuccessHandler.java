package com.MADA.mada_SeoulBike.global.auth.oauth2;

import com.MADA.mada_SeoulBike.domain.user.domain.UserRepository;
import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import com.MADA.mada_SeoulBike.global.auth.application.AuthService;
import com.MADA.mada_SeoulBike.global.auth.dto.TokenResponse;
import com.MADA.mada_SeoulBike.global.auth.jwt.TokenProvider;
import com.MADA.mada_SeoulBike.global.exception.MyErrorCode;
import com.MADA.mada_SeoulBike.global.exception.MyException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthService authService;
    @Value("${frontend.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email"); // 우리가 OAuth2User에 넣어둔 email

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new MyException(MyErrorCode.USER_NOT_FOUND));

        TokenResponse tokenResponse = authService.login(user);

        String redirectUrl = redirectUri + "?accessToken=" + tokenResponse.accessToken() +
                "&refreshToken=" + tokenResponse.refreshToken();

        // 환경에 맞는 URI로 리다이렉트
        response.sendRedirect(redirectUri);

    }
}


