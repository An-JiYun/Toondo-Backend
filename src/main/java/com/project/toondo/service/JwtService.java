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
    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private static final String SECRET_KEY = "YourFixedSecretKeyForJwt1234567890123456";
//    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

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

    /**
     * Authorization 헤더에서 JWT 추출
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Authorization 헤더가 없거나 잘못되었습니다.");
        }
        return authHeader.substring(7); // "Bearer " 이후의 토큰 반환
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 토큰 검증 및 파싱
                    .getBody();
            return claims.get("userId", Long.class); // userId 추출
        } catch (ExpiredJwtException e) {
            throw new JwtException("토큰이 만료되었습니다.", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 토큰 형식입니다.", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 형식입니다.", e);
        } catch (SignatureException e) {
            throw new JwtException("서명이 유효하지 않습니다.", e);
        } catch (JwtException e) {
            throw new JwtException("토큰 검증 실패: " + e.getMessage(), e);
        }
    }
}

