package com.example.gym.service;

import com.example.gym.dto.WorkoutSessionDto;
import com.example.gym.exception.OptimisticLockConflictException;
import com.example.gym.interceptor.Logged;
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
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.ForbiddenException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
@Logged
public class WorkoutSessionService {

    @Inject
    private WorkoutSessionRepository sessionRepository;

    @Inject
    private WorkoutTypeRepository typeRepository;

    @Inject
    private UserService userService;

    @Inject
    private EntityManager em;

    @Resource
    private EJBContext ejbContext;

    public Optional<WorkoutSession> findSessionById(UUID id) {
        return sessionRepository.findById(id);
    }

    public List<WorkoutSession> findSessionsByTypeId(UUID typeId) {
        if (ejbContext.isCallerInRole("admin")) {
            return sessionRepository.findByTypeId(typeId);
        }
        
        Principal principal = ejbContext.getCallerPrincipal();
        if (principal != null) {
            String username = principal.getName();
            Optional<User> currentUser = userService.findByUsername(username);
            
            if (currentUser.isPresent()) {
                UUID userId = currentUser.get().getId();
                return sessionRepository.findByTypeIdAndUserId(typeId, userId);
            }
        }
        
        return List.of();
    }

    /**
     * Filtrowanie sesji z dynamicznym Criteria API (AND logic).
     * Wszystkie parametry są opcjonalne - null/puste nie są uwzględniane w filtrze.
     */
    public List<WorkoutSession> findSessionsByFilters(UUID typeId, 
                                                       com.example.gym.model.enums.WorkoutStatus status,
                                                       LocalDateTime startTimeFrom, 
                                                       LocalDateTime startTimeTo,
                                                       LocalDateTime endTimeFrom, 
                                                       LocalDateTime endTimeTo) {
        UUID userId = null;
        
        // Dla nie-adminów dodaj filtr po userId
        if (!ejbContext.isCallerInRole("admin")) {
            Principal principal = ejbContext.getCallerPrincipal();
            if (principal != null) {
                Optional<User> currentUser = userService.findByUsername(principal.getName());
                if (currentUser.isPresent()) {
                    userId = currentUser.get().getId();
                } else {
                    return List.of();
                }
            } else {
                return List.of();
            }
        }
        
        return sessionRepository.findByFilters(typeId, userId, status, 
                                                startTimeFrom, startTimeTo, 
                                                endTimeFrom, endTimeTo);
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

        try {
            sessionRepository.save(session);
            em.flush(); // Wymusza natychmiastowy zapis, aby wykryć konflikt wersji
        } catch (OptimisticLockException e) {
            // Pobierz aktualną wersję z bazy danych
            WorkoutSession databaseVersion = sessionRepository.findById(session.getId())
                    .orElse(null);
            
            // Stwórz kopię danych użytkownika (session jest już detached po wyjątku)
            WorkoutSession userVersion = WorkoutSession.builder()
                    .id(session.getId())
                    .version(session.getVersion())
                    .workoutType(session.getWorkoutType())
                    .user(session.getUser())
                    .startTime(session.getStartTime())
                    .endTime(session.getEndTime())
                    .status(session.getStatus())
                    .createdAt(session.getCreatedAt())
                    .updatedAt(session.getUpdatedAt())
                    .build();
            
            throw new OptimisticLockConflictException(
                    "Session was modified by another user. Please review the changes.",
                    databaseVersion,
                    userVersion
            );
        }
    }

    public WorkoutSession createWorkoutSession(UUID typeId, WorkoutSessionDto dto) {
        WorkoutType type = typeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("WorkoutType not found"));

        Principal principal = ejbContext.getCallerPrincipal();
        User owner = null;
        if (principal != null) {
            owner = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        } else {
             throw new IllegalStateException("No authenticated user found");
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
            
            if (!ejbContext.isCallerInRole("admin")) {
                Principal principal = ejbContext.getCallerPrincipal();
                if (principal != null) {
                    User currentUser = userService.findByUsername(principal.getName())
                            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
                    
                    if (session.getUser() == null || !session.getUser().getId().equals(currentUser.getId())) {
                        throw new ForbiddenException("You can only update your own workout sessions");
                    }
                } else {
                    throw new IllegalStateException("No authenticated user found");
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

    public void deleteWorkoutSession(UUID id) {
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
                } else {
                    throw new IllegalStateException("No authenticated user found");
                }
            }
        }
        
        sessionRepository.delete(id);
    }
}
