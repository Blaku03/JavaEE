package com.example.gym.service;

import com.example.gym.dto.WorkoutTypeDto;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.repository.WorkoutTypeRepository;
import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class WorkoutTypeService {

    @Inject
    private WorkoutTypeRepository typeRepository;

    @Inject
    private WorkoutSessionService sessionService;

    @Resource
    private EJBContext ejbContext;

    public WorkoutTypeService() {
    }

    public WorkoutType createWorkoutType(String name, String description) {
        checkAdminRole();
        return createWorkoutTypeSystem(name, description);
    }

    public WorkoutType createWorkoutTypeSystem(String name, String description) {
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

    public void deleteWorkoutType(UUID typeId) {
        checkAdminRole();
        List<WorkoutSession> sessions = sessionService.findSessionsByTypeId(typeId);
        for (WorkoutSession session : sessions) {
            sessionService.deleteWorkoutSession(session.getId());
        }
        typeRepository.delete(typeId);
    }

    public Optional<WorkoutType> updateWorkoutType(UUID id, WorkoutTypeDto dto) {
        checkAdminRole();
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

    private void checkAdminRole() {
        if (!ejbContext.isCallerInRole("admin")) {
             throw new ForbiddenException("Only admin can perform this action");
        }
    }
}
