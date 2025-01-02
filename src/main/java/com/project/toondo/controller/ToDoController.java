package com.project.toondo.controller;

import com.project.toondo.dto.DailyToDoRequest;
import com.project.toondo.dto.DdayToDoRequest;
import com.project.toondo.service.ToDoService;
import com.project.toondo.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/todos")
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @Autowired
    private JwtService jwtService;

    // Dday Todo CRUD
    // 새로운 디데이 투두 생성
    @PostMapping("/dday/create")
    public ResponseEntity<?> createDdayToDo(HttpServletRequest request, @RequestBody DdayToDoRequest ddayRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.createDdayToDo(userId, ddayRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 날의 디데이 투두 리스트 조회
    @GetMapping("/dday/list/{date}")
    public ResponseEntity<?> getDdayToDos(HttpServletRequest request, @PathVariable String date) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getDdayToDos(userId, LocalDate.parse(date));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 디데이 투두의 상세 정보 조회
    @GetMapping("/dday/detail/{todoId}")
    public ResponseEntity<?> getDdayToDoDetails(HttpServletRequest request, @PathVariable Long todoId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getDdayToDoDetails(userId, todoId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 디데이 투두 수정
    @PutMapping("/dday/update/{todoId}")
    public ResponseEntity<?> updateDdayToDo(HttpServletRequest request, @PathVariable Long todoId, @RequestBody DdayToDoRequest ddayRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.updateDdayToDo(userId, todoId, ddayRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 디데이 투두 삭제
    @DeleteMapping("/dday/delete/{todoId}")
    public ResponseEntity<?> deleteDdayToDo(HttpServletRequest request, @PathVariable Long todoId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.deleteDdayToDo(userId, todoId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 디데이 투두 진행률 수정
    @PutMapping("/dday/update-status/{todoId}")
    public ResponseEntity<?> updateDdayStatus(HttpServletRequest request, @PathVariable Long todoId, @RequestParam Integer status) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.updateDdayStatus(userId, todoId, status);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




    // Daily Todo CRUD
    // 새로운 데일리 투두 생성
    @PostMapping("/daily/create")
    public ResponseEntity<?> createDailyToDo(HttpServletRequest request, @RequestBody DailyToDoRequest dailyRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.createDailyToDo(userId, dailyRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 날의 데일리 투두 조회
    @GetMapping("/daily/list/{date}")
    public ResponseEntity<?> getDailyToDos(HttpServletRequest request, @PathVariable String date) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getDailyToDos(userId, LocalDate.parse(date));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 데일리 투두의 상세 정보 조회
    @GetMapping("/daily/detail/{todoId}")
    public ResponseEntity<?> getDailyToDoDetails(HttpServletRequest request, @PathVariable Long todoId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getDailyToDoDetails(userId, todoId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 데일리 투두 수정
    @PutMapping("/daily/update/{todoId}")
    public ResponseEntity<?> updateDailyToDo(HttpServletRequest request, @PathVariable Long todoId, @RequestBody DailyToDoRequest dailyRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.updateDailyToDo(userId, todoId, dailyRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 데일리 투두 삭제
    @DeleteMapping("/daily/delete/{todoId}")
    public ResponseEntity<?> deleteDailyToDo(HttpServletRequest request, @PathVariable Long todoId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.deleteDailyToDo(userId, todoId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 데일리 투두 내일하기
    @PutMapping("/daily/move-tomorrow/{todoId}")
    public ResponseEntity<?> moveToTomorrow(HttpServletRequest request, @PathVariable Long todoId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            Map<String, Object> response = toDoService.moveToTomorrow(userId, todoId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 특정 데일리 투두 완료 체크하기 (true -> false, false -> true)
    @PutMapping("/daily/completed/{todoId}")
    public ResponseEntity<?> checkCompleted(HttpServletRequest request, @PathVariable Long todoId) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);
            
            Map<String, Object> response = toDoService.checkCompleted(userId, todoId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }





    ///////
    // 특정 날짜의 모든 할 일 조회
    @GetMapping("/all/{date}")
    public ResponseEntity<?> getAllToDosByDate(HttpServletRequest request, @PathVariable String date) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getAllToDosByDate(userId, LocalDate.parse(date));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //특정 달의 한 달치 모든 할 일 조회
    @GetMapping("/all/month/{yearMonth}")
    public ResponseEntity<?> getAllToDosByMonth(HttpServletRequest request, @PathVariable String yearMonth) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getAllToDosByMonth(userId, yearMonth);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
