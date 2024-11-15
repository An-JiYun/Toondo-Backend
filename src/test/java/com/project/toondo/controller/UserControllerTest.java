package com.project.toondo.controller;

import com.project.toondo.dto.UserLoginRequest;
import com.project.toondo.dto.UserSignupRequest;
import com.project.toondo.entity.Users;
import com.project.toondo.service.JwtService;
import com.project.toondo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void testSignupUser_Success() {
        UserSignupRequest userSignupRequest = new UserSignupRequest("testId", "testPassword", "안지윤");

        when(userService.signupUser(userSignupRequest)).thenReturn(new Users("testId", "testPassword", "안지윤"));

        ResponseEntity<String> response = userController.signupUser(userSignupRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("회원가입 성공");
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 로그인 ID")
    public void testSignupUser_Failure_DuplicateLoginId() {
        UserSignupRequest userSignupRequest = new UserSignupRequest("testId", "testPassword", "안지윤");

        when(userService.signupUser(userSignupRequest)).thenThrow(new IllegalArgumentException("이미 존재하는 로그인 ID입니다."));

        ResponseEntity<String> response = userController.signupUser(userSignupRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("이미 존재하는 로그인 ID입니다.");
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void testLoginUser_Success() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("testId", "testPassword");

        when(userService.loginUser(userLoginRequest)).thenReturn("mockJwtToken");

        ResponseEntity<String> response = userController.loginUser(userLoginRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer mockJwtToken");
        assertThat(response.getBody()).isEqualTo("로그인 성공");
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 로그인 정보")
    public void testLoginUser_Failure_InvalidCredentials() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("testId", "wrongPassword");

        when(userService.loginUser(userLoginRequest)).thenThrow(new IllegalArgumentException("로그인 실패: 잘못된 로그인 정보입니다."));

        ResponseEntity<String> response = userController.loginUser(userLoginRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("로그인 실패: 잘못된 로그인 정보입니다.");
    }

    @Test
    @DisplayName("내 정보 조회 성공 테스트")
    public void testGetMyInfo_Success() {
        long userId = 1L;
        Users user = new Users("testId", "testPassword", "안지윤");

        when(jwtService.getUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userController.getMyInfo();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Users.class);
        assertThat(((Users) response.getBody()).getLoginId()).isEqualTo("testId");
    }

    @Test
    @DisplayName("내 정보 조회 실패 테스트 - 사용자 없음")
    public void testGetMyInfo_Failure_UserNotFound() {
        long userId = 1L;

        when(jwtService.getUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userController.getMyInfo();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("내 정보 조회 실패 테스트 - 유효하지 않은 토큰")
    public void testGetMyInfo_Failure_InvalidToken() {
        when(jwtService.getUserId()).thenThrow(new RuntimeException("유효하지 않은 토큰입니다."));

        ResponseEntity<Object> response = userController.getMyInfo();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("유효하지 않은 토큰입니다.");
    }

    @Test
    @DisplayName("내 정보 수정 성공 테스트")
    public void testUpdateMyInfo_Success() {
        long userId = 1L;
        Users user = new Users("testId", "testPassword", "안지윤");

        // Mocking
        when(jwtService.getUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        // 요청 데이터
        Map<String, String> updateRequest = Map.of(
                "nickname", "newNickname",
                "password", "newPassword"
        );
        ResponseEntity<String> response = userController.updateMyInfo(updateRequest);

        // 결과 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("내 정보 수정 성공");
    }

    @Test
    @DisplayName("내 정보 수정 실패 테스트 - 사용자 없음")
    public void testUpdateMyInfo_Failure_UserNotFound() {
        long userId = 1L;

        // Mocking
        when(jwtService.getUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // 요청 데이터
        Map<String, String> updateRequest = Map.of(
                "nickname", "newNickname",
                "password", "newPassword"
        );

        // 컨트롤러 호출
        ResponseEntity<String> response = userController.updateMyInfo(updateRequest);

        // 결과 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("유효하지 않은 토큰입니다.");

    }
}
