package com.example.lab0.workoutsession.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorkoutSessionsResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WorkoutSession {
        private UUID id;
        private String name;
    }

    private List<WorkoutSession> workoutSessions;
}