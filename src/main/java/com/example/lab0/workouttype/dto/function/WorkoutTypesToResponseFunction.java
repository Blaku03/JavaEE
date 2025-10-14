package com.example.lab0.workouttype.dto.function;

import com.example.lab0.WorkoutType;
import com.example.lab0.workouttype.dto.GetWorkoutTypesResponse;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkoutTypesToResponseFunction implements Function<List<WorkoutType>, GetWorkoutTypesResponse> {
    @Override
    public GetWorkoutTypesResponse apply(List<WorkoutType> entities) {
        return GetWorkoutTypesResponse.builder()
                .workoutTypes(entities.stream()
                        .map(workoutType -> GetWorkoutTypesResponse.WorkoutType.builder()
                                .id(workoutType.getId())
                                .name(workoutType.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}