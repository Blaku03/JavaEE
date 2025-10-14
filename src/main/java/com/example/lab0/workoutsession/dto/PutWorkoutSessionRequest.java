package com.example.lab0.workoutsession.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PutWorkoutSessionRequest {
    private String name;
    private UUID workoutType;
    private UUID user;
}