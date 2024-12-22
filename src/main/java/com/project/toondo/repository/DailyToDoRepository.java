package com.project.toondo.repository;

import com.project.toondo.entity.DailyToDos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyToDoRepository extends JpaRepository<DailyToDos, Long> {
    // 특정 ID와  유저 ID를 기준으로 데일리 투두 디테일 조회
    @Query("SELECT d FROM DailyToDos d WHERE d.dailyTodoId = :todoId AND d.user.userId = :userId")
    Optional<DailyToDos> findByIdAndUserId(@Param("todoId") Long todoId, @Param("userId") Long userId);

    // 특정 userId와 특정 날짜의 활성화된 데일리 투두 조회
    @Query("SELECT d FROM DailyToDos d WHERE d.user.userId = :userId AND d.date = :date")
    List<DailyToDos> findDateDailyToDos(@Param("userId") Long userId, @Param("date") LocalDate date);


    @Query("SELECT d FROM DailyToDos d WHERE d.user.userId = :userId AND d.date = :date")
    List<DailyToDos> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);


    @Query("SELECT d FROM DailyToDos d WHERE d.user.userId = :userId AND d.date BETWEEN :startDate AND :endDate")
    List<DailyToDos> findByUserIdAndMonth(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}

