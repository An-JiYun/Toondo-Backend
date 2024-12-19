package com.project.toondo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deleted_todos")
public class DeletedToDos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deletedTodoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private Long todoId; // DdayToDos 또는 DailyToDos의 ID
    private LocalDate date; // 비활성화된 날짜
    private String type; // "Dday" 또는 "Daily"
}
