package com.project.toondo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyToDoRequest {
    private String description;
    private Long goalId;
    private LocalDate date; // 투두 날짜
    private Integer urgency;
    private Integer importance;
}
