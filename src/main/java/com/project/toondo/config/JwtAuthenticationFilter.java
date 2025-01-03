package com.project.toondo.config;

import com.project.toondo.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 요청 헤더 확인
            System.out.println("[Debug] Request Path: " + request.getRequestURI());
            System.out.println("[Debug] Request Method: " + request.getMethod());
            System.out.println("[Debug] Authorization Header: " + request.getHeader("Authorization"));

            String token = jwtService.extractTokenFromRequest(request); // 요청에서 JWT 추출
            if (token == null) {
                throw new JwtException("[Error] Authorization 헤더가 없거나 잘못되었습니다.");
            }

            Long userId = jwtService.getUserId(token); // JWT에서 userId 추출

            // SecurityContext에 인증 정보 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            System.err.println("[Error] JWT 검증 실패: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 로그인, 회원가입 경로는 필터 제외
        return path.startsWith("/users/check-phone-number") || path.startsWith("/users/login") || path.startsWith("/users/signup");
    }

}
