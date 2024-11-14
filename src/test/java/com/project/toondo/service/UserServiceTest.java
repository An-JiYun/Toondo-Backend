package com.project.toondo.service;

import com.project.toondo.dto.UserLoginRequest;
import com.project.toondo.dto.UserSignupRequest;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("회원가입 테스트")
    public void testSignupUser_Success() {
        UserSignupRequest userSignupRequest = new UserSignupRequest("testId", "testPassword", "안지윤");
        Users user = new Users(userSignupRequest.getLoginId(), userSignupRequest.getPassword(), userSignupRequest.getNickname());

        when(userRepository.findByLoginId(userSignupRequest.getLoginId())).thenReturn(Optional.empty());
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users savedUser = userService.signupUser(userSignupRequest);
        assertThat(savedUser.getLoginId()).isEqualTo("testId");
        assertThat(savedUser.getNickname()).isEqualTo("안지윤");
    }

    @Test
    @DisplayName("이미 존재하는 회원으로 인한 회원가입 실패 테스트")
    public void testSignupUser_ThrowsException_WhenUserExists() {
        UserSignupRequest userSignupRequest = new UserSignupRequest("testId", "testPassword", "안지윤");

        when(userRepository.findByLoginId(userSignupRequest.getLoginId())).thenReturn(Optional.of(new Users()));

        assertThatThrownBy(() -> userService.signupUser(userSignupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 로그인 ID입니다.");
    }


    @Test
    @DisplayName("로그인 성공 테스트")
    public void testLoginUser_Success() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("testId", "testPassword");
        Users user = new Users("testId", "testPassword", "안지윤");

        when(userRepository.findByLoginId(userLoginRequest.getLoginId())).thenReturn(Optional.of(user));
        when(jwtService.createJWT(user.getUserId())).thenReturn("mockJwtToken");

        String jwtToken = userService.loginUser(userLoginRequest);
        assertThat(jwtToken).isEqualTo("mockJwtToken");
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
    public void testLoginUser_FailsWithWrongPassword() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("testId", "wrongPassword");
        Users user = new Users("testId", "correctPassword", "안지윤");

        when(userRepository.findByLoginId(userLoginRequest.getLoginId())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.loginUser(userLoginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 회원")
    public void testLoginUser_FailsWithNonExistingUser() {
        UserLoginRequest userLoginRequest = new UserLoginRequest("nonExistingId", "testPassword");

        when(userRepository.findByLoginId(userLoginRequest.getLoginId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loginUser(userLoginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공 테스트")
    public void testGetUserById_Success() {
        Users user = new Users("testId", "testPassword", "안지윤");
        user.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<Users> foundUser = userService.getUserById(1L);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLoginId()).isEqualTo("testId");
        assertThat(foundUser.get().getNickname()).isEqualTo("안지윤");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 실패 테스트")
    public void testGetUserById_FailsWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Users> user = userService.getUserById(999L);
        assertThat(user).isNotPresent();
    }

    @Test
    @DisplayName("닉네임만 수정하는 테스트")
    public void testUpdateUser_NicknameOnly() {
        Long userId = 1L;
        Users user = new Users("testId", "oldPassword", "oldNickname");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUser(userId, "newNickname", null);

        assertThat(user.getNickname()).isEqualTo("newNickname");
        assertThat(user.getPassword()).isEqualTo("oldPassword");

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("비밀번호만 수정하는 테스트")
    public void testUpdateUser_PasswordOnly() {
        Long userId = 1L;
        Users user = new Users("testId", "oldPassword", "oldNickname");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUser(userId, null, "newPassword");

        assertThat(user.getNickname()).isEqualTo("oldNickname");
        assertThat(user.getPassword()).isEqualTo("newPassword");

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("닉네임과 비밀번호 모두 수정하는 테스트")
    public void testUpdateUser_NicknameAndPassword() {
        Long userId = 1L;
        Users user = new Users("testId", "oldPassword", "oldNickname");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUser(userId, "newNickname", "newPassword");

        assertThat(user.getNickname()).isEqualTo("newNickname");
        assertThat(user.getPassword()).isEqualTo("newPassword");

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("사용자 ID가 존재하지 않는 경우 예외 발생")
    public void testUpdateUser_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, "newNickname", "newPassword"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

}
