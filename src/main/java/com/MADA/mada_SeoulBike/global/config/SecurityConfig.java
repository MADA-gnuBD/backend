package com.MADA.mada_SeoulBike.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF 비활성화
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // 모든 요청 허용
                );
        return http.build();
    }
}