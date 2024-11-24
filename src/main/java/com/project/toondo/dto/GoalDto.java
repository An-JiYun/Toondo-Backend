package com.project.toondo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GoalDto {
    private Long goalId;
    private String goalName;
    private LocalDate startline;
    private LocalDate deadline;
    private Integer estimatedProgress;
    private Integer currentProgress;
    private LocalDateTime createdAt;
}