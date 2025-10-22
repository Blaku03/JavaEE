package com.example.gym.repository;

import com.example.gym.model.WorkoutSession;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WorkoutSessionRepository {

    private final Map<UUID, WorkoutSession> sessions = new ConcurrentHashMap<>();

    public void save(WorkoutSession session) {
        sessions.put(session.getId(), session);
    }

    public Optional<WorkoutSession> findById(UUID id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public List<WorkoutSession> findAll() {
        return List.copyOf(sessions.values());
    }
}