package com.project.toondo.controller;

import com.project.toondo.dto.GoalRequest;
import com.project.toondo.service.GoalService;
import com.project.toondo.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> createGoal(HttpServletRequest request, @RequestBody GoalRequest goalRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            Map<String, Object> response = goalService.createGoal(userId, goalRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

    // 2. 모든 목표 조회
    @GetMapping("/list")
    public ResponseEntity<?> getAllGoals(HttpServletRequest request) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            List<Map<String, Object>> response = goalService.getAllGoalsByUserId(userId);

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 특정 목표 조회
    @GetMapping("/detail/{goalId}")
    public ResponseEntity<?> getGoalById(HttpServletRequest request, @PathVariable Long goalId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            Map<String, Object> response = goalService.getGoalByIdAndUserId(goalId, userId);

            return ResponseEntity.ok(response);
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
    public ResponseEntity<?> deleteGoal(HttpServletRequest request, @PathVariable Long goalId) {
        try {
            Long userId = jwtService.getUserId(jwtService.extractTokenFromRequest(request));
            Map<String, String> response = goalService.deleteGoal(goalId, userId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("요청 처리 중 오류가 발생했습니다.");
        }
    }

    // 5. 목표 수정
    @PutMapping("/update/{goalId}")
    public ResponseEntity<?> updateGoal(HttpServletRequest request, @PathVariable Long goalId, @RequestBody GoalRequest goalRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            Map<String, Object> response = goalService.updateGoal(goalId, userId, goalRequest);

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }

}
