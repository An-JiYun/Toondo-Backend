package com.project.toondo.dto;

import com.project.toondo.entity.Goals;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoRequest {
    private Long todoId;
    private Long goalId;
    private String title;
    private Double status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer urgency;
    private Integer importance;
    private String comment;
}
