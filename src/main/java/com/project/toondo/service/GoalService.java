package com.project.toondo.service;

import com.project.toondo.dto.GoalRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.GoalRepository;
import com.project.toondo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    // 목표 생성
    public Goals createGoal(Long userId, GoalRequest goalRequest) {
        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 목표 생성
        Goals goal = new Goals(user, goalRequest.getGoalName(), goalRequest.getStartline(), goalRequest.getDeadline());
        return goalRepository.save(goal);
    }

    // 특정 사용자의 모든 목표 조회
    public List<Goals> getAllGoalsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return goalRepository.findByUserId(userId);
    }

    // 특정 사용자의 특정 목표 조회
    public Optional<Goals> getGoalByIdAndUserId(Long goalId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        return goalRepository.findByGoalIdAndUserId(goalId, userId);
    }

    // 목표 삭제
    public boolean deleteGoal(Long goalId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        Optional<Goals> goal = goalRepository.findByGoalIdAndUserId(goalId, userId);
        if (goal.isPresent()) {
            goalRepository.delete(goal.get());
            return true;
        }
        return false;
    }

    // 목표 수정
    public boolean updateGoal(Long goalId, Long userId, GoalRequest goalRequest) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        Optional<Goals> goalOptional = goalRepository.findByGoalIdAndUserId(goalId, userId);
        if (goalOptional.isPresent()) {
            Goals goal = goalOptional.get();
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
            return true;
        }
        return false;
    }
}
