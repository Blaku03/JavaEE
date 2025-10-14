package com.example.lab0.workoutsession.repository.memory;

import com.example.lab0.WorkoutSession;
import com.example.lab0.datastore.DataStore;
import com.example.lab0.workoutsession.repository.api.WorkoutSessionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkoutSessionInMemoryRepository implements WorkoutSessionRepository {

    private final DataStore store;

    public WorkoutSessionInMemoryRepository(DataStore store) {
        this.store = store;
    }

    @Override
    public List<WorkoutSession> findAll() {
        return store.findAllWorkoutSessions();
    }

    @Override
    public Optional<WorkoutSession> find(UUID id) {
        return store.findWorkoutSession(id);
    }

    @Override
    public void create(WorkoutSession session) {
        store.createWorkoutSession(session);
    }

    @Override
    public void delete(UUID id) {
        store.deleteWorkoutSession(id);
    }

    @Override
    public void update(WorkoutSession session) {
        store.updateWorkoutSession(session);
    }
}