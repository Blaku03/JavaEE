package com.example.gym.repository;

import com.example.gym.model.WorkoutType;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WorkoutTypeRepository {

    private final Map<UUID, WorkoutType> types = new ConcurrentHashMap<>();

    public void save(WorkoutType type) {
        types.put(type.getId(), type);
    }

    public Optional<WorkoutType> findById(UUID id) {
        return Optional.ofNullable(types.get(id));
    }

    public List<WorkoutType> findAll() {
        return List.copyOf(types.values());
    }
}