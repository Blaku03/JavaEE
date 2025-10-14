package com.example.lab0.workoutsession.dto.function;

import com.example.lab0.WorkoutSession;
import com.example.lab0.workoutsession.dto.GetWorkoutSessionsResponse;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkoutSessionsToResponseFunction implements Function<List<WorkoutSession>, GetWorkoutSessionsResponse> {
    @Override
    public GetWorkoutSessionsResponse apply(List<WorkoutSession> entities) {
        return GetWorkoutSessionsResponse.builder()
                .workoutSessions(entities.stream()
                        .map(session -> GetWorkoutSessionsResponse.WorkoutSession.builder()
                                .id(session.getId())
                                .name(session.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}