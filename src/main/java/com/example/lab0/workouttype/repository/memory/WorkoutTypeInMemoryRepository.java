package com.example.lab0.workouttype.repository.memory;

import com.example.lab0.WorkoutType;
import com.example.lab0.datastore.DataStore;
import com.example.lab0.workouttype.repository.api.WorkoutTypeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkoutTypeInMemoryRepository implements WorkoutTypeRepository {

    private final DataStore store;

    public WorkoutTypeInMemoryRepository(DataStore store) {
        this.store = store;
    }

    @Override
    public List<WorkoutType> findAll() {
        return store.findAllWorkoutTypes();
    }

    @Override
    public Optional<WorkoutType> find(UUID id) {
        return store.findWorkoutType(id);
    }

    @Override
    public void create(WorkoutType workoutType) {
        store.createWorkoutType(workoutType);
    }
}