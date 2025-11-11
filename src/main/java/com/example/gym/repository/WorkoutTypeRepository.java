package com.example.gym.repository;

import com.example.gym.model.WorkoutType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager; 
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class WorkoutTypeRepository {
    
    @Inject
    private EntityManager em;

    public void save(WorkoutType type) {
        em.merge(type);
    }

    public Optional<WorkoutType> findById(UUID id) {
        return Optional.ofNullable(em.find(WorkoutType.class, id));
    }

    public List<WorkoutType> findAll() {
        return em.createQuery("SELECT t FROM WorkoutType t", WorkoutType.class)
                .getResultList();
    }

    public void delete(UUID id) {
        findById(id).ifPresent(type -> {
            em.remove(type); 
        });
    }
}