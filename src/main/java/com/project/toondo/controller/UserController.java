package com.project.toondo.controller;

import com.project.toondo.dto.UserRequest;
import com.project.toondo.dto.UserUpdateRequest;
import com.project.toondo.service.JwtService;
import com.project.toondo.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/check-phone-number")
    public ResponseEntity<?> checkPhoneNumber(@RequestBody Map<String, Long> requestBody) {
        try {
            Long loginId = requestBody.get("loginId");
            boolean exists = userService.checkPhoneNumber(loginId);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody UserRequest userRequest) {
        try {
            Map<String, Object> response = userService.signupUser(userRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRequest userRequest) {
        try {
            Map<String, Object> response = userService.loginUser(userRequest);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 잘못된 로그인 정보입니다.");
        }
    }

    // 닉네임만 저장
    @PutMapping("/save-nickname")
    public ResponseEntity<?> saveNickname(HttpServletRequest request, @RequestBody Map<String, String> requestBody) {
        try {
            // JWT에서 사용자 ID 추출
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            // 요청에서 닉네임 추출
            String newNickname = requestBody.get("nickname");

            Map<String, Object> response = userService.saveNickname(userId, newNickname);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다.");
        }
    }

    // 내 정보 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyInfo(HttpServletRequest request) {
        try {
            // JWT에서 사용자 ID 추출
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = userService.getMyInfo(userId);

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            // JWT 검증 실패 시 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            // 기타 예외 발생 시 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다.");
        }
    }


    // 내 정보 수정
    @PutMapping("/update-my")
    public ResponseEntity<?> updateMyInfo(HttpServletRequest request, @RequestBody UserUpdateRequest updateRequest) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            // 서비스 호출하여 사용자 정보 업데이트
            Map<String, Object> response = userService.updateUser(userId, updateRequest);

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다.");
        }
    }

    // 로그아웃은 클라이언트 측에서 저장돤 jwtToken을 삭제
}