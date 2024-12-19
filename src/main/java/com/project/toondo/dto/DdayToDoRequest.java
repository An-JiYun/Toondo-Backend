package com.project.toondo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DdayToDoRequest {
    private String description;
    private Long goalId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer urgency;
    private Integer importance;
}
