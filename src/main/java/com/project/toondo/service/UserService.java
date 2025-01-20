package com.project.toondo.service;

import com.project.toondo.dto.UserRequest;
import com.project.toondo.dto.UserUpdateRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public boolean checkPhoneNumber(String loginId) {
        if (loginId == null) {
            throw new IllegalArgumentException("전화번호가 비어있습니다.");
        }
        return userRepository.findByLoginId(loginId).isPresent();
    }

    public Map<String, Object> signupUser(UserRequest userRequest) {
        if (userRepository.findByLoginId(userRequest.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다.");
        }

        Users user = new Users(userRequest.getLoginId(), userRequest.getPassword());
        userRepository.save(user);

        // JWT 토큰 생성
        String jwtToken = jwtService.createJWT(user.getUserId());

        // 응답 데이터 구성
        Map<String, Object> response = buildResponse("회원가입 성공", user);
        response.put("token", "Bearer " + jwtToken);

        return response;
    }

    public Map<String, Object> loginUser(UserRequest userRequest) {
        Users dbUser = userRepository.findByLoginId(userRequest.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!userRequest.getPassword().equals(dbUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String jwtToken = jwtService.createJWT(dbUser.getUserId());

        // 응답 데이터 구성
        Map<String, Object> response = buildResponse("로그인 성공", dbUser);
        response.put("token", "Bearer " + jwtToken);

        return response;
    }

    // 닉네임만 업데이트하는 메서드
    public Map<String, Object> saveNickname(Long userId, String nickname) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임 설정
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }

        user.setNickname(nickname);
        userRepository.save(user);

        return buildResponse("닉네임 저장 완료", user);
    }

    public Optional<Users> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Map<String, Object> getMyInfo(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return buildResponse("내 정보 조회 성공", user);
    }

    public Map<String, Object> updateUser(Long userId, UserUpdateRequest updateRequest) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (updateRequest.getNickname() != null && !updateRequest.getNickname().trim().isEmpty()) {
            user.setNickname(updateRequest.getNickname());
        }

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
            user.setPassword(updateRequest.getPassword());
        }

        userRepository.save(user);

        return buildResponse("내 정보 수정 성공", user);
    }

    // Goals 엔티티 -> Map 변환 메서드 (message 추가)
    private Map<String, Object> buildResponse(String message, Users user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("userId", user.getUserId());
        response.put("loginId", user.getLoginId());
        response.put("password", user.getPassword());
        response.put("nickname", user.getNickname());
        response.put("points", user.getPoints());
        return response;
    }

}
