package com.project.toondo.repository;

import com.project.toondo.entity.DdayToDos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DdayToDoRepository extends JpaRepository<DdayToDos, Long> {
    // 특정 유저 ID의 특정 날짜에 해당하는 디데이 투두 조회
    @Query("SELECT d FROM DdayToDos d WHERE d.user.userId = :userId " +
            "AND d.startDate <= :date AND d.endDate >= :date AND d.isActive = true")
    List<DdayToDos> findDateDdayToDos(@Param("userId") Long userId, @Param("date") LocalDate date);

    // 특정 ID와 유저 ID를 기준으로 디데이 투두 조회
    @Query("SELECT d FROM DdayToDos d WHERE d.ddayTodoId = :todoId AND d.user.userId = :userId")
    Optional<DdayToDos> findByIdAndUserId(@Param("todoId") Long todoId, @Param("userId") Long userId);


}