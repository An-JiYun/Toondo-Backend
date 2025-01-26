package com.project.toondo.controller;

import com.project.toondo.dto.DdayToDoRequest;
import com.project.toondo.dto.SyncRequest;
import com.project.toondo.dto.ToDoRequest;
import com.project.toondo.entity.ToDos;
import com.project.toondo.service.JwtService;
import com.project.toondo.service.ToDoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
public class ToDoController {
    @Autowired
    private ToDoService toDoService;

    @Autowired
    private JwtService jwtService;

    // 패치: 클라이언트에서 받은 ToDo 리스트를 DB에 저장
    @PostMapping("/all/fetch")
    public ResponseEntity<?> fetchTodos(HttpServletRequest request, @RequestBody SyncRequest syncRequest) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            List<ToDoRequest> toDoRequests = syncRequest.getToDoRequests();
            List<Long> deletedTodoIds = syncRequest.getDeletedTodoIds();

            Map<String, Object> response = toDoService.syncTodos(userId, toDoRequests, deletedTodoIds);

            System.out.println("[DEBUG] ToDo synchronization successful: " + response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 커밋: 특정 유저의 ToDo 리스트를 반환
    @GetMapping("/all/commit")
    public ResponseEntity<?> commitTodos(HttpServletRequest request) {
        try {
            String token = jwtService.extractTokenFromRequest(request);
            Long userId = jwtService.getUserId(token);

            Map<String, Object> response = toDoService.getTodosByUserId(userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
