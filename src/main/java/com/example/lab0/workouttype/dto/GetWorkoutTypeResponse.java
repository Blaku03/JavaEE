package com.example.lab0.workouttype.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorkoutTypeResponse {
    private UUID id;
    private String name;
    private String description;
    private int intensityLevel;
}