package com.project.toondo.service;

import com.project.toondo.dto.GoalRequest;
import com.project.toondo.dto.GoalDto;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.GoalRepository;
import com.project.toondo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public Goals createGoal(Long userId, GoalRequest goalRequest) {
        // 사용자 검증 및 조회
        Users user = validateUser(userId);

        // 목표 생성
        Goals goal = new Goals(user, goalRequest.getGoalName(), goalRequest.getStartline(), goalRequest.getDeadline());
        return goalRepository.save(goal);
    }

    // 특정 사용자의 모든 목표 조회
    public List<GoalDto> getAllGoalsByUserId(Long userId) {
        validateUser(userId);
        List<Goals> goals = goalRepository.findByUserUserId(userId);

        // DTO로 변환
        return goals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 사용자의 특정 목표 조회
    public Optional<GoalDto> getGoalByIdAndUserId(Long goalId, Long userId) {
        validateUser(userId);
        Optional<Goals> goal = goalRepository.findByGoalIdAndUserUserId(goalId, userId);

        // DTO로 변환
        return goal.map(this::convertToDto);
    }

    // 목표 삭제
    public boolean deleteGoal(Long goalId, Long userId) {
        validateUser(userId);

        Optional<Goals> goal = goalRepository.findByGoalIdAndUserUserId(goalId, userId);
        if (goal.isPresent()) {
            goalRepository.delete(goal.get());
            return true;
        }
        return false;
    }

    // 목표 수정
    public boolean updateGoal(Long goalId, Long userId, GoalRequest goalRequest) {
        validateUser(userId);

        Optional<Goals> goalOptional = goalRepository.findByGoalIdAndUserUserId(goalId, userId);
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

    // Goals 엔티티 -> GoalDto 변환 메서드
    private GoalDto convertToDto(Goals goal) {
        return new GoalDto(
                goal.getGoalId(),
                goal.getGoalName(),
                goal.getStartline(),
                goal.getDeadline(),
                goal.getEstimatedProgress(),
                goal.getCurrentProgress(),
                goal.getCreatedAt()
        );
    }

}
