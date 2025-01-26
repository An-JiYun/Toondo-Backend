package com.project.toondo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todos")
@IdClass(ToDoId.class) // 복합 키 클래스 설정
public class ToDos {

    @Id
    private Long todoId; // 프론트에서 제공하는 투두 ID

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 사용자 ID를 외래 키로 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = true)
    private Goals goal;

    @Column
    private String title;

    @Column
    private double status;

    @Column(length = 500)
    private String comment;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private int urgency;

    @Column
    private int importance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ToDos(Long todoId, Users user, Goals goal, String title, String comment,
                 LocalDate startDate, LocalDate endDate, Integer urgency, Integer importance) {
        this.todoId = todoId;
        this.user = user;
        this.goal = goal;
        this.title = title;
        this.comment = comment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.urgency = urgency;
        this.importance = importance;
        this.status = 0; // 초기값 설정
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
