package com.example.lab0.workoutsession.repository.api;

import com.example.lab0.WorkoutSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutSessionRepository {
    List<WorkoutSession> findAll();
    Optional<WorkoutSession> find(UUID id);
    void create(WorkoutSession session);
    void delete(UUID id);
    void update(WorkoutSession session);
}