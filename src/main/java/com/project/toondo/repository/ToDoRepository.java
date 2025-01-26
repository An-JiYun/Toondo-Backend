package com.project.toondo.repository;

import com.project.toondo.entity.ToDoId;
import com.project.toondo.entity.ToDos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDos, ToDoId> {
    List<ToDos> findByUserUserId(Long userId);
}
