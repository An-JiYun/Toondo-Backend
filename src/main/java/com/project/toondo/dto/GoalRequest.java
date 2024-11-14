package com.project.toondo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {
    private String goalName;
    private LocalDate startline;
    private LocalDate deadline;
}
