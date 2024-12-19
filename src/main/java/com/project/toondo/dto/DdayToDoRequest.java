package com.project.toondo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
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
