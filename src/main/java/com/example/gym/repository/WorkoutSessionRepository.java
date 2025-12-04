package com.example.gym.repository;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.enums.WorkoutStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkoutSession> cq = cb.createQuery(WorkoutSession.class);
        Root<WorkoutSession> root = cq.from(WorkoutSession.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    public void delete(UUID id) {
        findById(id).ifPresent(session -> {
            em.remove(session);
        });
    }

    public List<WorkoutSession> findByTypeId(UUID typeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkoutSession> cq = cb.createQuery(WorkoutSession.class);
        Root<WorkoutSession> root = cq.from(WorkoutSession.class);
        
        cq.select(root)
          .where(cb.equal(root.get("workoutType").get("id"), typeId));
        
        return em.createQuery(cq).getResultList();
    }

    public List<WorkoutSession> findByTypeIdAndUserId(UUID typeId, UUID userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkoutSession> cq = cb.createQuery(WorkoutSession.class);
        Root<WorkoutSession> root = cq.from(WorkoutSession.class);
        
        Predicate typeCondition = cb.equal(root.get("workoutType").get("id"), typeId);
        Predicate userCondition = cb.equal(root.get("user").get("id"), userId);
        
        cq.select(root).where(cb.and(typeCondition, userCondition));
        
        return em.createQuery(cq).getResultList();
    }

    /**
     * Dynamiczne filtrowanie sesji po wszystkich polach (AND logic).
     * Pola null/puste są ignorowane.
     */
    public List<WorkoutSession> findByFilters(UUID typeId, UUID userId, WorkoutStatus status,
                                               LocalDateTime startTimeFrom, LocalDateTime startTimeTo,
                                               LocalDateTime endTimeFrom, LocalDateTime endTimeTo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkoutSession> cq = cb.createQuery(WorkoutSession.class);
        Root<WorkoutSession> root = cq.from(WorkoutSession.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Filtr po typie treningu
        if (typeId != null) {
            predicates.add(cb.equal(root.get("workoutType").get("id"), typeId));
        }
        
        // Filtr po użytkowniku
        if (userId != null) {
            predicates.add(cb.equal(root.get("user").get("id"), userId));
        }
        
        // Filtr po statusie
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        
        // Filtr po czasie rozpoczęcia (zakres)
        if (startTimeFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startTimeFrom));
        }
        if (startTimeTo != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), startTimeTo));
        }
        
        // Filtr po czasie zakończenia (zakres)
        if (endTimeFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("endTime"), endTimeFrom));
        }
        if (endTimeTo != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTimeTo));
        }
        
        cq.select(root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        return em.createQuery(cq).getResultList();
    }
}