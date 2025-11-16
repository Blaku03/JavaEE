package com.example.gym.jsf;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.service.WorkoutService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@RequestScoped
public class WorkoutTypeController implements Serializable {

    @Inject
    private WorkoutService workoutService;

    @Getter
    private List<WorkoutType> workoutTypes;

    @Getter @Setter
    private String selectedTypeId;

    @Getter
    private WorkoutType selectedWorkoutType;

    @Getter
    private List<WorkoutSession> sessionsForType;

    @PostConstruct
    public void init() {
        workoutTypes = workoutService.findAllTypes();
    }

    public void loadSelectedCategory() {
        if (selectedTypeId != null && !selectedTypeId.isEmpty()) {
            try {
                UUID uuid = UUID.fromString(selectedTypeId);
                selectedWorkoutType = workoutService.findTypeById(uuid).orElse(null);

                if (selectedWorkoutType != null) {
                    sessionsForType = workoutService.findSessionsByTypeId(selectedWorkoutType.getId());
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Otrzymano nieprawidłowy format UUID: " + selectedTypeId);
                selectedWorkoutType = null;
            }
        }
    }

    public String deleteCategory(UUID id) {
        workoutService.deleteWorkoutType(id);
        return "categories.xhtml?faces-redirect=true";
    }

    public String deleteSession(UUID sessionId, UUID typeId) {
        workoutService.deleteWorkoutSession(sessionId);
        return "category_view.xhtml?id=" + typeId + "&faces-redirect=true";
    }
}