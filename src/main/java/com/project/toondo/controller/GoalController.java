package com.project.toondo.controller;

import com.project.toondo.dto.GoalRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.service.GoalService;
import com.project.toondo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private JwtService jwtService;

    // 1. 목표 등록
    @PostMapping("/create")
    public ResponseEntity<String> createGoal(@RequestHeader("Authorization") String token, @RequestBody GoalRequest goalRequest) {
        Long userId = jwtService.getUserId();  // JWT 토큰으로부터 userId 추출
        goalService.createGoal(userId, goalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("목표가 등록되었습니다.");
    }

    // 2. 모든 목표 조회
    @GetMapping("/list")
    public ResponseEntity<List<Goals>> getAllGoals(@RequestHeader("Authorization") String token) {
        Long userId = jwtService.getUserId();  // JWT 토큰으로부터 userId 추출
        List<Goals> goals = goalService.getAllGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    // 3. 특정 목표 조회
    @GetMapping("/detail/{goalId}")
    public ResponseEntity<Object> getGoalById(@RequestHeader("Authorization") String token, @PathVariable Long goalId) {
        Long userId = jwtService.getUserId();  // JWT 토큰으로부터 userId 추출
        Optional<Goals> goal = goalService.getGoalByIdAndUserId(goalId, userId);
        if (goal.isPresent()) {
            return ResponseEntity.ok(goal.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 목표를 찾을 수 없습니다.");
        }
    }

    // 4. 목표 삭제
    @DeleteMapping("/delete/{goalId}")
    public ResponseEntity<String> deleteGoal(@RequestHeader("Authorization") String token, @PathVariable Long goalId) {
        Long userId = jwtService.getUserId();  // JWT 토큰으로부터 userId 추출
        boolean isDeleted = goalService.deleteGoal(goalId, userId);
        if (isDeleted) {
            return ResponseEntity.ok("목표가 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 목표를 찾을 수 없습니다.");
        }
    }

    // 5. 목표 수정
    @PutMapping("/update/{goalId}")
    public ResponseEntity<String> updateGoal(@RequestHeader("Authorization") String token, @PathVariable Long goalId, @RequestBody GoalRequest goalRequest) {
        Long userId = jwtService.getUserId();  // JWT 토큰으로부터 userId 추출
        boolean isUpdated = goalService.updateGoal(goalId, userId, goalRequest);
        if (isUpdated) {
            return ResponseEntity.ok("목표가 수정되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 목표를 찾을 수 없습니다.");
        }
    }
}
