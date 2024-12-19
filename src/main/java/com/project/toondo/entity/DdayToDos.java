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
@Table(name = "dday_todos")
public class DdayToDos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dday_todo_id")
    private Long ddayTodoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = true)
    private Goals goal;

    private LocalDate startDate; // 시작일

    private LocalDate endDate;   // 종료일

    @Column(length = 1000, nullable = false)
    private String description;

    private Integer urgency;

    private Integer importance;

    private Integer status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DdayToDos(Users user, Goals goal, LocalDate startDate, LocalDate endDate,
                     String description, Integer urgency, Integer importance) {
        this.user = user;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.urgency = urgency;
        this.importance = importance;
        this.status = 0; // 초기값 설정
    }

    // 엔티티 생성 시 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
