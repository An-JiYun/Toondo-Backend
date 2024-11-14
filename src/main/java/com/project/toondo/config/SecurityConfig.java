package com.project.toondo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable()) // CSRF 비활성화
                .authorizeRequests()
                .requestMatchers("/users/signup", "/users/login").permitAll() // 회원가입, 로그인은 인증 필요 없음
                .requestMatchers("/users/my").authenticated() // '/users/my'는 인증 필요
                .anyRequest().authenticated(); // 그 외 나머지 요청도 인증 필요

        return http.build();
    }
}
