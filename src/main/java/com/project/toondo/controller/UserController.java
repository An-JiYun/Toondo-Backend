package com.project.toondo.controller;

import com.project.toondo.dto.UserLoginRequest;
import com.project.toondo.dto.UserSignupRequest;
import com.project.toondo.entity.Users;
import com.project.toondo.service.JwtService;
import com.project.toondo.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@RequestBody UserSignupRequest userSignupRequest) {
        try {
            userService.signupUser(userSignupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        try {
            String jwtToken = userService.loginUser(userLoginRequest);
            return ResponseEntity.ok().header("Authorization", "Bearer " + jwtToken).body("로그인 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 잘못된 로그인 정보입니다.");
        }
    }

    // 내 정보 조회
    @GetMapping("/my")
    public ResponseEntity<Object> getMyInfo() {  // 명시적으로 Object를 반환
        try {
            long userId = jwtService.getUserId();
            Optional<Users> user = userService.getUserById(userId);

            // 사용자가 존재할 경우 Users 객체를 반환, 없을 경우 오류 메시지 반환
            return user.<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }


    // 내 정보 수정
    @PutMapping("/update-my")
    public ResponseEntity<String> updateMyInfo(@RequestParam(required = false) String nickname,
                                               @RequestParam(required = false) String password) {
        try {
            long userId = jwtService.getUserId();
            Optional<Users> user = userService.getUserById(userId);

            if (user.isPresent()) {
                userService.updateUser(userId, nickname, password);
                return ResponseEntity.ok("내 정보 수정 성공");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

    // 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpSession session) {
//        session.invalidate();
//        return ResponseEntity.ok("로그아웃 성공");
//    }
}