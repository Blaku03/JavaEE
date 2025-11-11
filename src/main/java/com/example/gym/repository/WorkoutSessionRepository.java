package com.example.gym.repository;

import com.example.gym.model.WorkoutSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager; // Import JPA
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class WorkoutSessionRepository {

    @Inject
    private EntityManager em;

    public void save(WorkoutSession session) {
        em.merge(session);
    }

    public Optional<WorkoutSession> findById(UUID id) {
        return Optional.ofNullable(em.find(WorkoutSession.class, id));
    }

    public List<WorkoutSession> findAll() {
        return em.createQuery("SELECT s FROM WorkoutSession s", WorkoutSession.class)
                .getResultList();
    }

    public void delete(UUID id) {
        findById(id).ifPresent(session -> {
            em.remove(session);
        });
    }

    public List<WorkoutSession> findByTypeId(UUID typeId) {
        return em.createQuery("SELECT s FROM WorkoutSession s WHERE s.workoutType.id = :typeId", WorkoutSession.class)
                .setParameter("typeId", typeId)
                .getResultList();
    }
}