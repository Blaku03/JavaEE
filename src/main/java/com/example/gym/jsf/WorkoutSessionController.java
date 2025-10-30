package com.example.gym.jsf;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.service.WorkoutService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class WorkoutSessionController implements Serializable {

    @Inject
    private WorkoutService workoutService;

    @Getter @Setter
    private String sessionId;

    @Getter @Setter
    private String typeId;

    @Getter
    private WorkoutSession session;

    @Getter
    private List<WorkoutType> allTypes;

    public void loadSessionForEdit() {
        allTypes = workoutService.findAllTypes();

        if ("new".equals(sessionId)) {
            session = new WorkoutSession();
            session.setStatus(WorkoutStatus.PLANNED);
            session.setStartTime(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0));
            if (typeId != null) {
                workoutService.findTypeById(UUID.fromString(typeId))
                        .ifPresent(session::setWorkoutType);
            }
        } else {
            if (sessionId != null) {
                session = workoutService.findSessionById(UUID.fromString(sessionId))
                        .orElse(new WorkoutSession());
            }
        }
    }

    public void loadSessionForView() {
        if (sessionId != null) {
            session = workoutService.findSessionById(UUID.fromString(sessionId))
                    .orElse(null);
        }
    }

    public String saveSession() {
        workoutService.saveWorkoutSession(session);
        return "category_view.xhtml?id=" + session.getWorkoutType().getId() + "&faces-redirect=true";
    }

    public WorkoutStatus[] getWorkoutStatuses() {
        return WorkoutStatus.values();
    }
}