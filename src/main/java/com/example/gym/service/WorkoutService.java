package com.example.gym.service;

import com.example.gym.dto.WorkoutSessionDto;
import com.example.gym.dto.WorkoutTypeDto;
import com.example.gym.model.User;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.repository.WorkoutSessionRepository;
import com.example.gym.repository.WorkoutTypeRepository;
import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.ForbiddenException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class WorkoutService {

    @Inject
    private WorkoutTypeRepository typeRepository;

    @Inject
    private WorkoutSessionRepository sessionRepository;

    
    @Inject
    private EntityManager em;

    @Resource
    private EJBContext ejbContext;

    @Inject
    private UserService userService;

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
        List<WorkoutSession> sessions = sessionRepository.findByTypeId(typeId);
        
        // If user is admin, return all sessions
        if (ejbContext.isCallerInRole("admin")) {
            return sessions;
        }
        
        // If user is regular user, filter to show only their sessions
        Principal principal = ejbContext.getCallerPrincipal();
        if (principal != null) {
            String username = principal.getName();
            Optional<User> currentUser = userService.findByUsername(username);
            
            if (currentUser.isPresent()) {
                UUID userId = currentUser.get().getId();
                return sessions.stream()
                    .filter(s -> s.getUser() != null && s.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
            }
        }
        
        // If no user context, return empty list
        return List.of();
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
        // Verify ownership unless user is admin
        if (!ejbContext.isCallerInRole("admin")) {
            Optional<WorkoutSession> sessionOpt = sessionRepository.findById(id);
            if (sessionOpt.isPresent()) {
                WorkoutSession session = sessionOpt.get();
                Principal principal = ejbContext.getCallerPrincipal();
                
                if (principal != null) {
                    User currentUser = userService.findByUsername(principal.getName())
                            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
                    
                    if (session.getUser() == null || !session.getUser().getId().equals(currentUser.getId())) {
                        throw new ForbiddenException("You can only delete your own workout sessions");
                    }
                }
            }
        }
        
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

        // Get current user and assign as owner
        Principal principal = ejbContext.getCallerPrincipal();
        User owner = null;
        if (principal != null) {
            owner = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        }

        WorkoutSession session = WorkoutSession.builder()
                .id(UUID.randomUUID()) 
                .workoutType(type) 
                .user(owner)
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
            
            // Verify ownership unless user is admin
            if (!ejbContext.isCallerInRole("admin")) {
                Principal principal = ejbContext.getCallerPrincipal();
                if (principal != null) {
                    User currentUser = userService.findByUsername(principal.getName())
                            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
                    
                    if (session.getUser() == null || !session.getUser().getId().equals(currentUser.getId())) {
                        throw new ForbiddenException("You can only update your own workout sessions");
                    }
                }
            }
            
            session.setStartTime(dto.getStartTime());
            session.setEndTime(dto.getEndTime());
            session.setStatus(dto.getStatus());
            
            sessionRepository.save(session);
            return Optional.of(session);
        }
        return Optional.empty();
    }
}