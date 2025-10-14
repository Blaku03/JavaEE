package com.example.lab0.workoutsession.dto.function;

import com.example.lab0.WorkoutSession;
import com.example.lab0.workoutsession.dto.GetWorkoutSessionResponse;
import java.util.function.Function;

public class WorkoutSessionToResponseFunction implements Function<WorkoutSession, GetWorkoutSessionResponse> {
    @Override
    public GetWorkoutSessionResponse apply(WorkoutSession entity) {
        return GetWorkoutSessionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .startTime(entity.getStartTime())
                .status(entity.getStatus())
                .workoutTypeId(entity.getWorkoutType().getId())
                .build();
    }
}