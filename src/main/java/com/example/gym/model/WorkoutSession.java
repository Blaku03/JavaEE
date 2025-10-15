package com.example.gym.model;

import com.example.gym.model.enums.WorkoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSession implements Serializable {
    private UUID id;
    private WorkoutType workoutType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private WorkoutStatus status;
}