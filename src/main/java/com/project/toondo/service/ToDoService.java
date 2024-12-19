package com.project.toondo.service;

import com.project.toondo.dto.DdayToDoRequest;
import com.project.toondo.entity.*;
import com.project.toondo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ToDoService {

    @Autowired
    private DdayToDoRepository ddayToDoRepository;

    @Autowired
    private DailyToDoRepository dailyToDoRepository;

    @Autowired
    private DeletedToDoRepository deletedToDoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    // 사용자 검증
    private Users validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // Dday ToDo CRUD*
    // 새로운 디데이 투두 생성
    public Map<String, Object> createDdayToDo(Long userId, DdayToDoRequest request) {
        try {
            Users user = validateUser(userId);

            // goalId 확인 후 Goal 객체 설정
            Goals goal = null;
            if (request.getGoalId() != null) {
                goal = goalRepository.findById(request.getGoalId()).orElse(null);
            }

            DdayToDos todo = new DdayToDos(user, goal, request.getStartDate(), request.getEndDate(),
                    request.getDescription(), request.getUrgency(), request.getImportance());

            ddayToDoRepository.save(todo);

            return buildResponse("Dday ToDo 생성 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 생성 실패: " + e.getMessage());
        }
    }

    // 특정 날의 디데이 투두 리스트 조회
    public Map<String, Object> getDdayToDos(Long userId, LocalDate date) {
        try {
            List<DdayToDos> todos = ddayToDoRepository.findDateDdayToDos(userId, date);

            return buildResponse("Dday ToDo 리스트 조회 성공", todos);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 리스트 조회 실패: " + e.getMessage());
        }
    }

    // 특정 디데이 투두의 상세 정보 조회
    public Map<String, Object> getDdayToDoDetails(Long userId, Long todoId) {
        try {
            DdayToDos todo = ddayToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Dday ToDo를 찾을 수 없습니다."));

            return buildResponse("Dday ToDo 상세 조회 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 상세 조회 실패: " + e.getMessage());
        }
    }

    // 특정 디데이 투두 수정
    public Map<String, Object> updateDdayToDo(Long userId, Long todoId, DdayToDoRequest request) {
        try {
            DdayToDos todo = ddayToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Dday ToDo를 찾을 수 없습니다."));

            // goalId 확인 및 업데이트
            if (request.getGoalId() != null) {
                if (request.getGoalId() == 0) { // goalId가 null이 아니라 0과 같은 유효하지 않은 값일 경우 예외 처리
                    todo.setGoal(null);
                } else {
                    Goals goal = goalRepository.findById(request.getGoalId())
                            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Goal ID입니다."));
                    todo.setGoal(goal);
                }
            } else {
                todo.setGoal(null); // goalId가 null이면 Goal 필드를 null로 설정
            }

            // 필드 업데이트: null이 아닌 필드만 반영
            if (request.getDescription() != null) {
                todo.setDescription(request.getDescription());
            }
            if (request.getStartDate() != null) {
                todo.setStartDate(request.getStartDate());
            }
            if (request.getEndDate() != null) {
                todo.setEndDate(request.getEndDate());
            }
            if (request.getUrgency() != null) {
                todo.setUrgency(request.getUrgency());
            }
            if (request.getImportance() != null) {
                todo.setImportance(request.getImportance());
            }

            ddayToDoRepository.save(todo);

            return buildResponse("Dday ToDo 수정 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 수정 실패: " + e.getMessage());
        }
    }

    // 특정 디데이 투두 삭제
    public Map<String, Object> deleteDdayToDo(Long userId, Long todoId) {
        try {
            ddayToDoRepository.deleteById(todoId);
            return buildResponse("Dday ToDo 삭제 성공", null);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 삭제 실패: " + e.getMessage());
        }
    }


    // 디데이 투두 진행률 수정


    // Daily Todo CRUD
    // 새로운 데일리 투두 생성

    public List<Map<String, Object>> getToDosByDate(Long userId, LocalDate date) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 삭제된 투두 ID 조회
        List<Long> deletedTodoIds = deletedToDoRepository.findDeletedTodoIdsByUserIdAndDate(userId, date);

        // 디데이 투두 조회 (삭제된 항목 제외)
        ddayToDoRepository.findDateDdayToDos(userId, date).stream()
                .filter(todo -> !deletedTodoIds.contains(todo.getDdayTodoId()))
                .forEach(todo -> result.add(buildResponse("디데이 투두 조회 성공", todo)));

        // 데일리 투두 조회 (삭제된 항목 제외)
        dailyToDoRepository.findDateDailyToDos(userId, date).stream()
                .filter(todo -> !deletedTodoIds.contains(todo.getDailyTodoId()))
                .forEach(todo -> result.add(buildResponse("데일리 투두 조회 성공", todo)));

        return result;
    }

    // 특정 todoId 상세 정보 조회
    public Map<String, Object> getToDoDetails(Long userId, Long todoId, String type) {
        if ("Dday".equalsIgnoreCase(type)) {
            DdayToDos todo = ddayToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("디데이 투두를 찾을 수 없습니다."));
            return buildResponse("디데이 투두 조회 성공", todo);

        } else {
            DailyToDos todo = dailyToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("데일리 투두를 찾을 수 없습니다."));
            return buildResponse("데일리 투두 조회 성공", todo);
        }
    }

    // 특정 할 일 삭제
    public Map<String, String> deleteToDoForDate(Long userId, Long todoId, String type, LocalDate date) {
        Users user = validateUser(userId);

        if ("Dday".equalsIgnoreCase(type)) {
            DdayToDos todo = ddayToDoRepository.findById(todoId)
                    .orElseThrow(() -> new IllegalArgumentException("디데이 투두를 찾을 수 없습니다."));
            saveDeletedToDo(user, todoId, type, date);
        } else if ("Daily".equalsIgnoreCase(type)) {
            DailyToDos todo = dailyToDoRepository.findById(todoId)
                    .orElseThrow(() -> new IllegalArgumentException("데일리 투두를 찾을 수 없습니다."));
            saveDeletedToDo(user, todoId, type, date);
        } else {
            throw new IllegalArgumentException("유효하지 않은 타입입니다.");
        }
        // 메시지만 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "투두리스트가 성공적으로 삭제되었습니다.");

        return response;
    }

    // DeletedToDos 테이블에 저장
    private void saveDeletedToDo(Users user, Long todoId, String type, LocalDate date) {
        DeletedToDos deletedToDo = new DeletedToDos();
        deletedToDo.setUser(user);
        deletedToDo.setTodoId(todoId);
        deletedToDo.setType(type);
        deletedToDo.setDate(date);
        deletedToDoRepository.save(deletedToDo);
    }

    // 특정 할 일 수정
    public Map<String, Object> updateToDo(Long userId, Long todoId, ToDoRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            DdayToDos todo = ddayToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("디데이 투두를 찾을 수 없습니다."));

            if (request.getGoalId() != null) {
                Goals goal = goalRepository.findById(request.getGoalId()).orElse(null);
                todo.setGoal(goal);
            }

            todo.setDescription(request.getDescription());
            todo.setStartDate(request.getStartDate());
            todo.setEndDate(request.getEndDate());
            todo.setUrgency(request.getUrgency());
            todo.setImportance(request.getImportance());

            ddayToDoRepository.save(todo);
            return buildResponse("디데이 투두 수정 성공", todo);

        } else {
            DailyToDos todo = dailyToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("데일리 투두를 찾을 수 없습니다."));

            if (request.getGoalId() != null) {
                Goals goal = goalRepository.findById(request.getGoalId()).orElse(null);
                todo.setGoal(goal);
            }

            todo.setDescription(request.getDescription());
            todo.setUrgency(request.getUrgency());
            todo.setImportance(request.getImportance());

            dailyToDoRepository.save(todo);
            return buildResponse("데일리 투두 수정 성공", todo);
        }
    }

    // 응답 생성
    private Map<String, Object> buildResponse(String message, Object todo) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);

        if (todo instanceof DdayToDos ddayToDo) {
            response.put("todoId", ddayToDo.getDdayTodoId());
            response.put("description", ddayToDo.getDescription());
            response.put("startDate", ddayToDo.getStartDate());
            response.put("endDate", ddayToDo.getEndDate());
            response.put("urgency", ddayToDo.getUrgency());
            response.put("importance", ddayToDo.getImportance());
            response.put("status", ddayToDo.getStatus());
        } else if (todo instanceof DailyToDos dailyToDo) {
            response.put("todoId", dailyToDo.getDailyTodoId());
            response.put("description", dailyToDo.getDescription());
            response.put("urgency", dailyToDo.getUrgency());
            response.put("importance", dailyToDo.getImportance());
            response.put("completed", dailyToDo.isCompleted());
        }

        return response;
    }

}
