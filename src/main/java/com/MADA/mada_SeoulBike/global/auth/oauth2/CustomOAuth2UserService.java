package com.MADA.mada_SeoulBike.global.auth.oauth2;


import com.MADA.mada_SeoulBike.domain.user.domain.UserRepository;
import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId(); // google, kakao
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // provider별로 정보 추출
        String email;
        String providerId;

        if (registrationId.equals("google")) {
            email = (String) attributes.get("email");
            providerId = (String) attributes.get("sub");
        } else if (registrationId.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            providerId = String.valueOf(attributes.get("id"));
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        // 이메일 기준 사용자 조회 또는 회원가입
        Users user = userRepository.findByEmail(email).orElseGet(() -> {
            Users newUser = Users.builder()
                    .userId(registrationId + "_" + providerId) // 유니크 ID 생성
                    .password("SOCIAL_LOGIN_USER") // 비번은 사용 안함
                    .userName("소셜사용자") // 기본값 설정
                    .nickname(null)
                    .email(email)
                    .provider(registrationId)
                    .providerId(providerId)
                    .deleted(false)
                    .build();
            return userRepository.save(newUser);
        });

        // JWT 발급 시 사용할 email 포함된 OAuth2User 반환
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("email", user.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                customAttributes,
                "email"
        );
    }
}
