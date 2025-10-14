package com.example.lab0.workouttype.dto.function;

import com.example.lab0.WorkoutType;
import com.example.lab0.workouttype.dto.GetWorkoutTypeResponse;
import java.util.function.Function;

public class WorkoutTypeToResponseFunction implements Function<WorkoutType, GetWorkoutTypeResponse> {
    @Override
    public GetWorkoutTypeResponse apply(WorkoutType entity) {
        return GetWorkoutTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .intensityLevel(entity.getIntensityLevel())
                .build();
    }
}