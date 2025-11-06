package com.example.gym.dto;

import com.example.gym.model.enums.WorkoutStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WorkoutSessionDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private WorkoutStatus status;
}