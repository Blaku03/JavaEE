package com.example.gym.service;

import com.example.gym.model.User;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.repository.UserRepository;
import com.example.gym.repository.WorkoutSessionRepository;
import com.example.gym.repository.WorkoutTypeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class WorkoutService {

    @Inject
    private WorkoutTypeRepository typeRepository;

    @Inject
    private WorkoutSessionRepository sessionRepository;

    @Inject
    private UserRepository userRepository;

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

    public Optional<WorkoutSession> createWorkoutSession(UUID userId, UUID typeId, WorkoutSession details) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<WorkoutType> typeOpt = typeRepository.findById(typeId);

        if (userOpt.isPresent() && typeOpt.isPresent()) {
            User user = userOpt.get();

            WorkoutSession session = WorkoutSession.builder()
                    .id(UUID.randomUUID())
                    .workoutType(typeOpt.get())
                    .startTime(details.getStartTime())
                    .endTime(details.getEndTime())
                    .status(details.getStatus())
                    .build();

            sessionRepository.save(session);

            user.getWorkoutSessions().add(session);
            userRepository.save(user);

            return Optional.of(session);
        }
        return Optional.empty();
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
}