package com.project.toondo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private SecretKey key;;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createJWT(Long userId){
        String token = Jwts.builder()
                .claim("userId", userId)
                .signWith(key)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1일 유효
                .compact();

        System.out.println("[Debug] Generated JWT Token: " + token);  // 디버깅: 생성된 JWT 토큰 출력
        return token;
    }

    /**
     * Authorization 헤더에서 JWT 추출
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("[Error] Authorization 헤더가 없거나 잘못되었습니다.");
        }
        return authHeader.substring(7); // "Bearer " 이후의 토큰 반환
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserId(String token) {
        Jws<Claims> claims = parseToken(token); // 토큰 검증
        return claims.getBody().get("userId", Long.class);
    }

    // JWT 파싱 로직
    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT 토큰이 만료되었습니다.", e);
        } catch (SignatureException e) {
            throw new JwtException("JWT 서명이 유효하지 않습니다.", e);
        } catch (Exception e) {
            throw new JwtException("JWT 형식이 잘못되었습니다.", e);
        }
    }
}

