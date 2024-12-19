package com.project.toondo.repository;

import com.project.toondo.entity.DeletedToDos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DeletedToDoRepository extends JpaRepository<DeletedToDos, Long> {

    // 특정 userId와 날짜에 해당하는 삭제된 투두 ID 조회
    @Query("SELECT d.todoId FROM DeletedToDos d WHERE d.user.userId = :userId AND d.date = :date")
    List<Long> findDeletedTodoIdsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
