package com.project.toondo.service;

import com.project.toondo.dto.GoalRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.GoalRepository;
import com.project.toondo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    // 사용자 검증
    private Users validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
    // 목표 생성
    public Map<String, Object> createGoal(Long userId, GoalRequest goalRequest) {
        // 사용자 검증 및 조회
        Users user = validateUser(userId);

        // 목표 생성
        Goals goal = new Goals(user, goalRequest.getGoalName(), goalRequest.getStartline(), goalRequest.getDeadline());
        goalRepository.save(goal);

        // 응답 데이터 구성
        return buildResponse("목표 생성 성공.", goal);
    }

    // 특정 사용자의 모든 목표 조회
    public List<Map<String, Object>> getAllGoalsByUserId(Long userId) {
        validateUser(userId);
        List<Goals> goals = goalRepository.findByUserUserId(userId);

        return goals.stream()
                .map(goal -> buildResponse("목표 전체 조회 성공", goal))
                .collect(Collectors.toList());
    }

    // 특정 사용자의 특정 목표 조회
    public Map<String, Object> getGoalByIdAndUserId(Long goalId, Long userId) {
        validateUser(userId);
        Goals goal = goalRepository.findByGoalIdAndUserUserId(goalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));

        return buildResponse("특정 목표 조회 성공", goal);
    }

    // 목표 삭제
    public Map<String, String> deleteGoal(Long goalId, Long userId) {
        validateUser(userId);

        Goals goal = goalRepository.findByGoalIdAndUserUserId(goalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));

        goalRepository.delete(goal);

        // 메시지만 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "목표가 성공적으로 삭제되었습니다.");

        return response;
    }

    // 목표 수정
    public Map<String, Object> updateGoal(Long goalId, Long userId, GoalRequest goalRequest) {
        validateUser(userId);

        Goals goal = goalRepository.findByGoalIdAndUserUserId(goalId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));

        if (goalRequest.getGoalName() != null) {
            goal.setGoalName(goalRequest.getGoalName());
        }
        if (goalRequest.getStartline() != null) {
            goal.setStartline(goalRequest.getStartline());
        }
        if (goalRequest.getDeadline() != null) {
            goal.setDeadline(goalRequest.getDeadline());
        }
        goalRepository.save(goal);

        return buildResponse("목표가 성공적으로 수정되었습니다.", goal);
    }

    // Goals 엔티티 -> Map 변환 메서드 (message 추가)
    private Map<String, Object> buildResponse(String message, Goals goal) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("goalId", goal.getGoalId());
        response.put("goalName", goal.getGoalName());
        response.put("startline", goal.getStartline());
        response.put("deadline", goal.getDeadline());
        response.put("estimatedProgress", goal.getEstimatedProgress());
        response.put("currentProgress", goal.getCurrentProgress());
        response.put("createdAt", goal.getCreatedAt());
        return response;
    }

}
