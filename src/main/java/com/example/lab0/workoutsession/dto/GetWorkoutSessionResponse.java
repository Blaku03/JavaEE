package com.example.lab0.workoutsession.dto;

import com.example.lab0.WorkoutStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorkoutSessionResponse {
    private UUID id;
    private String name;
    private LocalDateTime startTime;
    private WorkoutStatus status;
    private UUID workoutTypeId;
}