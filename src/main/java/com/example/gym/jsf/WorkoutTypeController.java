package com.example.gym.jsf;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.service.WorkoutSessionService;
import com.example.gym.service.WorkoutTypeService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ForbiddenException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class WorkoutTypeController implements Serializable {

    @Inject
    private WorkoutTypeService typeService;

    @Inject
    private WorkoutSessionService sessionService;

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
        workoutTypes = typeService.findAllTypes();
    }

    public void loadSelectedCategory() {
        if (selectedTypeId != null && !selectedTypeId.isEmpty()) {
            try {
                UUID uuid = UUID.fromString(selectedTypeId);
                selectedWorkoutType = typeService.findTypeById(uuid).orElse(null);

                if (selectedWorkoutType != null) {
                    sessionsForType = sessionService.findSessionsByTypeId(selectedWorkoutType.getId());
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Otrzymano nieprawidłowy format UUID: " + selectedTypeId);
                selectedWorkoutType = null;
            }
        }
    }

    public String deleteCategory(UUID id) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Brak uprawnień", "Tylko administrator może usuwać kategorie."));
            return null;
        }
        try {
            typeService.deleteWorkoutType(id);
            return "categories.xhtml?faces-redirect=true";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd", "Nie udało się usunąć kategorii."));
            return null;
        }
    }

    public String deleteSession(UUID sessionId, UUID typeId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            sessionService.deleteWorkoutSession(sessionId);
            return "category_view.xhtml?id=" + typeId + "&faces-redirect=true";
        } catch (ForbiddenException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Brak uprawnień", "Nie możesz usunąć tej sesji."));
            return null;
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd", "Nie udało się usunąć sesji."));
            return null;
        }
    }

    public void deleteSessionAjax(UUID sessionId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            sessionService.deleteWorkoutSession(sessionId);
            // Refresh the sessions list after deletion
            if (selectedWorkoutType != null) {
                sessionsForType = sessionService.findSessionsByTypeId(selectedWorkoutType.getId());
            }
        } catch (ForbiddenException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Brak uprawnień", "Nie możesz usunąć tej sesji."));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd", "Nie udało się usunąć sesji."));
        }
    }

    public void deleteCategoryAjax(UUID id) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Brak uprawnień", "Tylko administrator może usuwać kategorie."));
            return;
        }
        try {
            typeService.deleteWorkoutType(id);
            // Refresh the categories list after deletion
            workoutTypes = typeService.findAllTypes();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd", "Nie udało się usunąć kategorii."));
        }
    }
}