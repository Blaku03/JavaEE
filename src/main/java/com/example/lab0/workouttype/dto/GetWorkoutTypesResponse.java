package com.example.lab0.workouttype.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorkoutTypesResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WorkoutType {
        private UUID id;
        private String name;
    }

    private List<WorkoutType> workoutTypes;
}