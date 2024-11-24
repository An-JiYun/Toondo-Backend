package com.project.toondo.controller;

import com.project.toondo.dto.GoalDto;
import com.project.toondo.dto.GoalRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.service.GoalService;
import com.project.toondo.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private JwtService jwtService;

    // 1. 목표 등록
    @PostMapping("/create")
    public ResponseEntity<String> createGoal(HttpServletRequest request, @RequestBody GoalRequest goalRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            goalService.createGoal(userId, goalRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("목표가 등록되었습니다.");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

    // 2. 모든 목표 조회
    @GetMapping("/list")
    public ResponseEntity<List<GoalDto>> getAllGoals(HttpServletRequest request) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            List<GoalDto> goals = goalService.getAllGoalsByUserId(userId)
                    .stream()
                    .map(goal -> new GoalDto(
                            goal.getGoalId(),
                            goal.getGoalName(),
                            goal.getStartline(),
                            goal.getDeadline(),
                            goal.getEstimatedProgress(),
                            goal.getCurrentProgress(),
                            goal.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(goals);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 3. 특정 목표 조회
    @GetMapping("/detail/{goalId}")
    public ResponseEntity<Object> getGoalById(HttpServletRequest request, @PathVariable Long goalId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            Optional<GoalDto> goal = goalService.getGoalByIdAndUserId(goalId, userId)
                    .map(g -> new GoalDto(
                            g.getGoalId(),
                            g.getGoalName(),
                            g.getStartline(),
                            g.getDeadline(),
                            g.getEstimatedProgress(),
                            g.getCurrentProgress(),
                            g.getCreatedAt()
                    ));

            // 목표가 존재하면 반환, 없으면 NOT_FOUND 반환
            return goal.<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 목표를 찾을 수 없습니다."));
        } catch (JwtException e) {
            // JWT 검증 실패 시 UNAUTHORIZED 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류가 발생했습니다.");
        }
    }

    // 4. 목표 삭제
    @DeleteMapping("/delete/{goalId}")
    public ResponseEntity<String> deleteGoal(HttpServletRequest request, @PathVariable Long goalId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            boolean isDeleted = goalService.deleteGoal(goalId, userId);
            if (isDeleted) {
                return ResponseEntity.ok("목표가 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 목표를 찾을 수 없습니다.");
            }
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

    // 5. 목표 수정
    @PutMapping("/update/{goalId}")
    public ResponseEntity<String> updateGoal(HttpServletRequest request, @PathVariable Long goalId, @RequestBody GoalRequest goalRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            boolean isUpdated = goalService.updateGoal(goalId, userId, goalRequest);
            if (isUpdated) {
                return ResponseEntity.ok("목표가 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 목표를 찾을 수 없습니다.");
            }
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

}
