package com.project.toondo.service;

import com.project.toondo.dto.ToDoRequest;
import com.project.toondo.entity.Goals;
import com.project.toondo.entity.ToDoId;
import com.project.toondo.entity.ToDos;
import com.project.toondo.entity.Users;
import com.project.toondo.repository.GoalRepository;
import com.project.toondo.repository.ToDoRepository;
import com.project.toondo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ToDoService {
    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    // 사용자 검증
    private Users validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    public Map<String, Object> syncTodos(Long userId, List<ToDoRequest> toDoRequests, List<Long> deletedTodoIds) {
        Users user = validateUser(userId);
//        List<Map<String, Object>> savedTodos = new ArrayList<>();

        int savedTodosCount = 0;
        for (ToDoRequest request : toDoRequests) {
//            System.out.println("[DEBUG] Processing ToDoRequest: " + request);

            ToDoId toDoId = new ToDoId(request.getTodoId(), userId);
            ToDos todo = toDoRepository.findById(toDoId)
                    .orElse(new ToDos());

            Goals goal = null;
            if (request.getGoalId() != null) {
                goal = goalRepository.findById(request.getGoalId())
                        .orElseThrow(() -> new IllegalArgumentException("Goal not found: " + request.getGoalId()));
            }

            todo.setTodoId(request.getTodoId());
            todo.setUser(user);
            todo.setGoal(goal);
            todo.setTitle(request.getTitle());
            todo.setStatus(request.getStatus());
            todo.setStartDate(request.getStartDate());
            todo.setEndDate(request.getEndDate());
            todo.setUrgency(request.getUrgency());
            todo.setImportance(request.getImportance());
            todo.setComment(request.getComment());

            toDoRepository.save(todo);
            savedTodosCount++;
//            savedTodos.add(buildResponse("개별 투두 성공적Todo .", todo));
        }
        System.out.println("[DEBUG] Saved todos count: " + savedTodosCount);

        // 2. 삭제된 투두 처리
        int deletedCount = 0;
        if (deletedTodoIds != null && !deletedTodoIds.isEmpty()) {
            for (Long todoId : deletedTodoIds) {
//                System.out.println("[DEBUG] Deleting ToDo with ID: " + todoId);

                ToDoId toDoId = new ToDoId(todoId, userId);
                if (toDoRepository.existsById(toDoId)) {
                    toDoRepository.deleteById(toDoId);
                    deletedCount++;
                }
            }
        }
        System.out.println("[DEBUG] Deleted todos count: " + deletedCount);


        // 최종 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "투두 동기화 저장 성공");
        response.put("savedTodosCount", savedTodosCount);
        response.put("deletedCount", deletedCount);


        return response;
    }

    public Map<String, Object> getTodosByUserId(Long userId) {
        List<ToDos> todos = toDoRepository.findByUserUserId(userId);

        if (todos == null) {
            todos = new ArrayList<>(); // 빈 리스트로 초기화
        }
        if (todos.isEmpty()) {
            throw new IllegalArgumentException("해당 유저의 투두 리스트가 없습니다.");
        }

        // 투두 리스트를 응답 형식으로 변환
        List<Map<String, Object>> todoResponses = todos.stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());

        // 최종 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "유저 투두리스트 전체 조회 성공");
        response.put("todos", todoResponses);
        response.put("count", todos.size());
        return response;

    }

    // 응답 생성 메서드
    private Map<String, Object> buildResponse(ToDos todo) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("todoId", todo.getTodoId());
        response.put("goalId", todo.getGoal() != null ? todo.getGoal().getGoalId() : null);
        response.put("title", todo.getTitle());
        response.put("startDate", todo.getStartDate());
        response.put("endDate", todo.getEndDate());
        response.put("urgency", todo.getUrgency());
        response.put("importance", todo.getImportance());
        response.put("status", todo.getStatus());
        response.put("comment", todo.getComment());
        return response;
    }

}
