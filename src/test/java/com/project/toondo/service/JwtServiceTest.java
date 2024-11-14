package com.project.toondo.service;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    @DisplayName("JWT 생성 테스트")
    void testCreateJWT() {
        long id = 1;
        String jwt = jwtService.createJWT(id);

        assertThat(jwt).isNotNull();
        assertThat(jwt).startsWith("eyJ");
    }

    @Test
    @DisplayName("JWT GET 테스트")
    void testGetJWT() {
        String token = "Bearer testToken";
        when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        String jwt = jwtService.getJWT();

        assertThat(jwt).isEqualTo("testToken");
    }

    @Test
    @DisplayName("JWT 토큰으로 멤버 아이디 가져오기")
    void testGetuserId() {
        long id = 1;
        String jwt = jwtService.createJWT(id);

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        long usesrId = jwtService.getUserId();

        assertThat(usesrId).isEqualTo(id);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 userId 가져오기")
    void testGetuserIdWithInvalidToken() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        assertThatThrownBy(() -> jwtService.getUserId())
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("null 토큰으로 userId 가져오기")
    void testGetuserIdWithNullToken() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        assertThatThrownBy(() -> jwtService.getUserId())
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("토큰이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("빈 토큰으로 userId 가져오기")
    void testGetuserIdWithEmptyToken() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer ");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        assertThatThrownBy(() -> jwtService.getUserId())
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("토큰이 유효하지 않습니다.");
    }
}