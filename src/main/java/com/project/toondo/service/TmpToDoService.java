package com.project.toondo.service;

import com.project.toondo.dto.DdayToDoRequest;
import com.project.toondo.dto.DailyToDoRequest;
import com.project.toondo.entity.*;
import com.project.toondo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TmpToDoService {

    @Autowired
    private DdayToDoRepository ddayToDoRepository;

    @Autowired
    private DailyToDoRepository dailyToDoRepository;

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
            validateUser(userId);
            List<DdayToDos> todos = ddayToDoRepository.findDateDdayToDos(userId, date);

            // 응답을 담을 리스트 생성
            List<Map<String, Object>> todoList = new ArrayList<>();

            for (DdayToDos todo : todos) {
                Map<String, Object> singleTodoResponse = buildResponse("Success", todo);
                todoList.add(singleTodoResponse);
            }

            // 최종 응답 생성
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Dday ToDo 리스트 조회 성공");
            response.put("todos", todoList);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 리스트 조회 실패: " + e.getMessage());
        }
    }

    // 특정 디데이 투두의 상세 정보 조회
    public Map<String, Object> getDdayToDoDetails(Long userId, Long todoId) {
        try {
            validateUser(userId);
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
            validateUser(userId);
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
            validateUser(userId);
            ddayToDoRepository.deleteById(todoId);
            return buildResponse("Dday ToDo 삭제 성공", null);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 삭제 실패: " + e.getMessage());
        }
    }


    // 디데이 투두 진행률 수정
    public Map<String, Object> updateDdayStatus(Long userId, Long todoId, Integer status) {
        try {
            validateUser(userId);
            DdayToDos todo = ddayToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Dday ToDo를 찾을 수 없습니다."));

            todo.setStatus(status);

            ddayToDoRepository.save(todo);

            return buildResponse("Dday ToDo 진행률 수정 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Dday ToDo 진행률 수정 실패: " + e.getMessage());
        }
    }


    // Daily Todo CRUD
    // 새로운 데일리 투두 생성
    public Map<String, Object> createDailyToDo(Long userId, DailyToDoRequest request) {
        try {
            Users user = validateUser(userId);

            // goalId 확인 후 Goal 객체 설정
            Goals goal = null;
            if (request.getGoalId() != null) {
                goal = goalRepository.findById(request.getGoalId()).orElse(null);
            }

            DailyToDos todo = new DailyToDos(user, goal, request.getDate(),
                    request.getDescription(), request.getUrgency(), request.getImportance());

            dailyToDoRepository.save(todo);

            return buildResponse("Daily ToDo 생성 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 생성 실패: " + e.getMessage());
        }
    }


    // 특정 날의 데일리 투두 조회
    public Map<String, Object> getDailyToDos(Long userId, LocalDate date) {
        try {
            validateUser(userId);
            List<DailyToDos> todos = dailyToDoRepository.findDateDailyToDos(userId, date);

            // 응답을 담을 리스트 생성
            List<Map<String, Object>> todoList = new ArrayList<>();

            for (DailyToDos todo : todos) {
                Map<String, Object> singleTodoResponse = buildResponse("Success", todo);
                todoList.add(singleTodoResponse);
            }

            // 최종 응답 생성
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Daily ToDo 리스트 조회 성공");
            response.put("todos", todoList);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 리스트 조회 실패: " + e.getMessage());
        }
    }

    // 특정 데일리 투두의 상세 정보 조회
    public Map<String, Object> getDailyToDoDetails(Long userId, Long todoId) {
        try {
            validateUser(userId);
            DailyToDos todo = dailyToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Daily ToDo를 찾을 수 없습니다."));

            return buildResponse("Daily ToDo 상세 조회 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 상세 조회 실패: " + e.getMessage());
        }
    }

    // 특정 데일리 투두 수정
    public Map<String, Object> updateDailyToDo(Long userId, Long todoId, DailyToDoRequest request) {
        try {
            validateUser(userId);
            DailyToDos todo = dailyToDoRepository.findByIdAndUserId(todoId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Daily ToDo를 찾을 수 없습니다."));

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
            if (request.getDate() != null) {
                todo.setDate(request.getDate());
            }
            if (request.getUrgency() != null) {
                todo.setUrgency(request.getUrgency());
            }
            if (request.getImportance() != null) {
                todo.setImportance(request.getImportance());
            }

            dailyToDoRepository.save(todo);

            return buildResponse("Daily ToDo 수정 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 수정 실패: " + e.getMessage());
        }
    }
    
    // 특정 데일리 투두 삭제
    public Map<String, Object> deleteDailyToDo(Long userId, Long todoId) {
        try {
            validateUser(userId);
            dailyToDoRepository.deleteById(todoId);
            return buildResponse("Daily ToDo 삭제 성공", null);
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 삭제 실패: " + e.getMessage());
        }
    }


    // 특정 데일리 투두 내일하기
    public Map<String, Object> moveToTomorrow(Long userId, Long todoId) {
        try {
            validateUser(userId);
            DailyToDos todo = dailyToDoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("데일리 투두를 찾을 수 없습니다."));
            
            todo.setDate(todo.getDate().plusDays(1)); // 날짜를 하루 증가
            dailyToDoRepository.save(todo);

            return buildResponse("Daily ToDo 내일하기 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 내일하기 실패: " + e.getMessage());
        }
    }

    //  특정 데일리 투두 완료 체크하기 (true -> false, false -> true)
    public Map<String, Object> checkCompleted(Long userId, Long todoId) {
        try {
            validateUser(userId);
            DailyToDos todo = dailyToDoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("데일리 투두를 찾을 수 없습니다."));
            
            todo.setCompleted(!todo.isCompleted());// (true -> false, false -> true)
        
            dailyToDoRepository.save(todo);

            return buildResponse("Daily ToDo 완료 체크 성공", todo);
        } catch (Exception e) {
            throw new RuntimeException("Daily ToDo 왼료 체크 실패: " + e.getMessage());
        }
    }


    // 특정 날짜의 모든 할 일 조회
    public Map<String, Object> getAllToDosByDate(Long userId, LocalDate date) {
        try {
            validateUser(userId);

            List<DailyToDos> dailyToDos = dailyToDoRepository.findByUserIdAndDate(userId, date);
            List<DdayToDos> ddayToDos = ddayToDoRepository.findByDate(userId, date);

            // 리스트를 각각 변환
            List<Map<String, Object>> dailyToDoResponses = dailyToDos.stream()
                    .map(todo -> buildResponse("Success", todo))
                    .collect(Collectors.toList());

            List<Map<String, Object>> ddayToDoResponses = ddayToDos.stream()
                    .map(todo -> buildResponse("Success", todo))
                    .collect(Collectors.toList());

            // 응답 데이터 구성
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("date", date);
            response.put("dailyToDos", dailyToDoResponses);
            response.put("ddayToDos", ddayToDoResponses);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("특정 날짜의 모든 할 일 조회 실패: " + e.getMessage());
        }
    }

    public Map<String, Object> getAllToDosByMonth(Long userId, String yearMonth) {
        if (yearMonth == null || yearMonth.isEmpty()) {
            throw new IllegalArgumentException("yearMonth는 필수 입력값이며, 'YYYY-MM' 형식이어야 합니다.");
        }
        try {
            validateUser(userId);

            YearMonth ym = YearMonth.parse(yearMonth);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();

            List<DailyToDos> dailyToDos = dailyToDoRepository.findByUserIdAndMonth(userId, startDate, endDate);
            List<DdayToDos> ddayToDos = ddayToDoRepository.findByMonth(userId, startDate, endDate);

            // 리스트를 각각 변환
            List<Map<String, Object>> dailyToDoResponses = dailyToDos.stream()
                    .map(todo -> buildResponse("Success", todo))
                    .collect(Collectors.toList());

            List<Map<String, Object>> ddayToDoResponses = ddayToDos.stream()
                    .map(todo -> buildResponse("Success", todo))
                    .collect(Collectors.toList());

            // 응답 데이터 구성
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("month", yearMonth);
            response.put("dailyToDos", dailyToDoResponses);
            response.put("ddayToDos", ddayToDoResponses);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("특정 달의 모든 할 일 조회 실패: " + e.getMessage());
        }
    }





    // 응답 생성
    private Map<String, Object> buildResponse(String message, Object todo) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);

        if (todo instanceof DdayToDos ddayToDo) {
            response.put("todoId", ddayToDo.getDdayTodoId());
            response.put("goalId", ddayToDo.getGoal());
            response.put("description", ddayToDo.getDescription());
            response.put("startDate", ddayToDo.getStartDate());
            response.put("endDate", ddayToDo.getEndDate());
            response.put("urgency", ddayToDo.getUrgency());
            response.put("importance", ddayToDo.getImportance());
            response.put("status", ddayToDo.getStatus());
        } else if (todo instanceof DailyToDos dailyToDo) {
            response.put("todoId", dailyToDo.getDailyTodoId());
            response.put("goalId", dailyToDo.getGoal());
            response.put("description", dailyToDo.getDescription());
            response.put("urgency", dailyToDo.getUrgency());
            response.put("importance", dailyToDo.getImportance());
            response.put("completed", dailyToDo.isCompleted());
        }

        return response;
    }

}
