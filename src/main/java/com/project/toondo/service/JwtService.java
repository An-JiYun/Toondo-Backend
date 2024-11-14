package com.project.toondo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
//    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String SECRET_KEY = "YourFixedSecretKeyForJwt1234567890123456";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String createJWT(Long userId){
        String token = Jwts.builder()
                .claim("userId", userId)
                .signWith(key)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 10000))
                .compact();

        System.out.println("Generated JWT Token: " + token);  // 디버깅: 생성된 JWT 토큰 출력
        return token;
    }

    public String getJWT() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String token = request.getHeader("Authorization");

        System.out.println("Authorization Header Token: " + token);  // 디버깅: Authorization 헤더에서 읽은 토큰 출력

        // token이 null이거나 "Bearer "만 포함된 경우 예외를 발생시킵니다.
        if (token == null || token.trim().isEmpty() || token.equals("Bearer ")) {
            throw new JwtException("토큰이 유효하지 않습니다.");
        }

        return token.replace("Bearer ", "");
    }

    public Long getUserId(){
        String accessToken = getJWT();
        System.out.println("Access Token for Validation: " + accessToken);  // 디버깅: 검증용 JWT 출력

        Jws<Claims> jws;
        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
        } catch (JwtException e) {
            System.err.println("Invalid JWT: " + e.getMessage());  // 디버깅: JWT 검증 실패 메시지
            throw new RuntimeException("토큰이 유효하지 않습니다.", e);
        }

        Long userId = jws.getBody().get("userId", Long.class);  // Long 타입으로 userId 추출
        System.out.println("Extracted userId from JWT: " + userId);  // 디버깅: 추출된 userId 출력
        return userId;
    }
}

