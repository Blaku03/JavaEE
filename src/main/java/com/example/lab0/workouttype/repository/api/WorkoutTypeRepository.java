package com.example.lab0.workouttype.repository.api;

import com.example.lab0.WorkoutType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutTypeRepository {
    List<WorkoutType> findAll();
    Optional<WorkoutType> find(UUID id);
    void create(WorkoutType workoutType);
}