package com.MADA.mada_SeoulBike.global.auth.jwt;

import com.MADA.mada_SeoulBike.domain.user.domain.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private Key key;

    @PostConstruct
    public void init() {
        // secret 값으로부터 Key 객체 생성 (HS256 알고리즘)
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken(Duration duration, Users user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + duration.toMillis());

        return Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }


    // Refresh Token 생성 (예: 7일 유효)
    public String generateRefreshToken(Users user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + Duration.ofDays(7).toMillis());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 로깅 추가 가능
            return false;
        }
    }

    // 토큰에서 사용자 이메일(주체) 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 인증 객체 반환 (Spring Security와 연동)
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);

        // UserDetailsService 또는 별도 DB 조회 방식으로 사용자 정보 로드
        // 여기서는 임시 권한 ROLE_USER 지정
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                email, // principal
                token, // credentials
                authorities
        );
    }
}