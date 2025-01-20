package com.project.toondo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "goals")
public class Goals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "goal_name", nullable = false, length = 255)
    private String goalName;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private Integer progress;

    @Column
    private boolean isCompleted;

    @Column
    private Integer status; // 0:진행중, 1:완료, 2:포기

    @Column
    private String icon;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 생성자
    public Goals(Users user, String goalName, LocalDate startDate, LocalDate endDate, String icon) {
        this.user = user;
        this.goalName = goalName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.icon = icon;
    }

    // 엔티티 생성 시 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
