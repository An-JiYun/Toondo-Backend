package com.project.toondo.controller;

import com.project.toondo.dto.UserLoginRequest;
import com.project.toondo.dto.UserSignupRequest;
import com.project.toondo.entity.Users;
import com.project.toondo.service.JwtService;
import com.project.toondo.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Object> getMyInfo(HttpServletRequest request) {
        try {
            // JWT에서 사용자 ID 추출
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            // 사용자 정보 조회
            Optional<Users> user = userService.getUserById(userId);

            // 사용자 정보 반환
            return user.<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
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
    public ResponseEntity<String> updateMyInfo(HttpServletRequest request, @RequestBody Map<String, String> updateRequest) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            // 요청에서 닉네임과 비밀번호 추출
            String newNickname = updateRequest.get("nickname");
            String newPassword = updateRequest.get("password");

            // 닉네임과 비밀번호가 모두 null인지 확인
            if ((newNickname == null || newNickname.trim().isEmpty()) &&
                    (newPassword == null || newPassword.trim().isEmpty())) {
                return ResponseEntity.badRequest().body("변경할 닉네임 또는 비밀번호를 제공해야 합니다.");
            }

            // 서비스 호출하여 사용자 정보 업데이트
            userService.updateUser(userId, newNickname, newPassword);
            return ResponseEntity.ok("내 정보가 수정되었습니다.");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다.");
        }
    }

    // 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpSession session) {
//        session.invalidate();
//        return ResponseEntity.ok("로그아웃 성공");
//    }
}