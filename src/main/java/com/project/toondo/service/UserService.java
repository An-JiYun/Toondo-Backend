package com.project.toondo.service;

import com.project.toondo.dto.UserSignupRequest;
import com.project.toondo.dto.UserLoginRequest;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public Users signupUser(UserSignupRequest userSignupRequest) {
        if (userRepository.findByLoginId(userSignupRequest.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다.");
        }

        Users user = new Users(userSignupRequest.getLoginId(), userSignupRequest.getPassword(), userSignupRequest.getNickname());
        return userRepository.save(user);
    }

    public String loginUser(UserLoginRequest userLoginRequest) {
        Users dbUser = userRepository.findByLoginId(userLoginRequest.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!userLoginRequest.getPassword().equals(dbUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Long userId = dbUser.getUserId();
        return jwtService.createJWT(userId);
    }

    public Optional<Users> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public void updateUser(Long userId, String nickname, String password) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }

        userRepository.save(user);
    }
}
