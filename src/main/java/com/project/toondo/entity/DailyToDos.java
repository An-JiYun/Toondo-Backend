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
@Table(name = "daily_todos")
public class DailyToDos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_todo_id")
    private Long dailyTodoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = true)
    private Goals goal;

    @Column(length = 1000, nullable = false)
    private String description;

    private LocalDate date;

    private Integer urgency;

    private Integer importance;

    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DailyToDos(Users user, Goals goal, LocalDate date,
                    String description, Integer urgency, Integer importance) {
        this.user = user;
        this.goal = goal;
        this.date = date;
        this.description = description;
        this.urgency = urgency;
        this.importance = importance;
        this.completed = false; // 초기값 설정
    }


    // 엔티티 생성 시 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
