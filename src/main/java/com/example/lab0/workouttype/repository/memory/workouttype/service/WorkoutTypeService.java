package com.example.lab0.workouttype.service;

import com.example.lab0.WorkoutType;
import com.example.lab0.workouttype.repository.api.WorkoutTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkoutTypeService {

    private final WorkoutTypeRepository repository;

    public WorkoutTypeService(WorkoutTypeRepository repository) {
        this.repository = repository;
    }

    public List<WorkoutType> findAll() {
        return repository.findAll();
    }

    public Optional<WorkoutType> find(UUID id) {
        return repository.find(id);
    }
}