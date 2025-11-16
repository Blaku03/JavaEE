package com.example.gym.service;

import com.example.gym.dto.WorkoutSessionDto;
import com.example.gym.dto.WorkoutTypeDto;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.repository.WorkoutSessionRepository;
import com.example.gym.repository.WorkoutTypeRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class WorkoutService {

    @Inject
    private WorkoutTypeRepository typeRepository;

    @Inject
    private WorkoutSessionRepository sessionRepository;

    
    @Inject
    private EntityManager em;

    public WorkoutService() {
    }

    public WorkoutType createWorkoutType(String name, String description) {
        WorkoutType type = WorkoutType.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .build();
        typeRepository.save(type);
        return type;
    }

    public List<WorkoutType> findAllTypes() {
        return typeRepository.findAll();
    }

    public Optional<WorkoutType> findTypeById(UUID id) {
        return typeRepository.findById(id);
    }

    public Optional<WorkoutSession> findSessionById(UUID id) {
        return sessionRepository.findById(id);
    }

    public List<WorkoutSession> findSessionsByTypeId(UUID typeId) {
        return sessionRepository.findByTypeId(typeId);
    }

    public void saveWorkoutSession(WorkoutSession session) {
        if (session.getId() == null) {
            session.setId(UUID.randomUUID());
        }

        if (session.getWorkoutType() != null && !em.contains(session.getWorkoutType())) {
            WorkoutType managedType = typeRepository.findById(session.getWorkoutType().getId())
                    .orElseThrow(() -> new IllegalArgumentException("WorkoutType not found during save"));
            session.setWorkoutType(managedType);
        }

        sessionRepository.save(session);
    }

    public void deleteWorkoutSession(UUID id) {
        sessionRepository.delete(id);
    }

    public void deleteWorkoutType(UUID typeId) {
        List<WorkoutSession> sessions = sessionRepository.findByTypeId(typeId);
        for (WorkoutSession session : sessions) {
            this.deleteWorkoutSession(session.getId());
        }
        typeRepository.delete(typeId);
    }

    public Optional<WorkoutType> updateWorkoutType(UUID id, WorkoutTypeDto dto) {
        Optional<WorkoutType> typeOpt = typeRepository.findById(id);
        if (typeOpt.isPresent()) {
            WorkoutType type = typeOpt.get();
            type.setName(dto.getName());
            type.setDescription(dto.getDescription());
            typeRepository.save(type); 
            return Optional.of(type);
        }
        return Optional.empty(); 
    }

    public WorkoutSession createWorkoutSession(UUID typeId, WorkoutSessionDto dto) {
        
        WorkoutType type = typeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("WorkoutType not found"));

        WorkoutSession session = WorkoutSession.builder()
                .id(UUID.randomUUID()) 
                .workoutType(type) 
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(dto.getStatus())
                .build();

        sessionRepository.save(session);
        return session;
    }

    public Optional<WorkoutSession> updateWorkoutSession(UUID sessionId, WorkoutSessionDto dto) {
        Optional<WorkoutSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            WorkoutSession session = sessionOpt.get();
            session.setStartTime(dto.getStartTime());
            session.setEndTime(dto.getEndTime());
            session.setStatus(dto.getStatus());
            
            sessionRepository.save(session);
            return Optional.of(session);
        }
        return Optional.empty();
    }
}