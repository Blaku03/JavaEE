package com.example.gym.jsf;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.service.WorkoutSessionService;
import com.example.gym.service.WorkoutTypeService;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class WorkoutSessionController implements Serializable {

    @Inject
    private WorkoutSessionService sessionService;

    @Inject
    private WorkoutTypeService typeService;

    @Getter @Setter
    private String sessionId;

    @Getter @Setter
    private String typeId;

    @Getter
    private WorkoutSession session;

    @Getter
    private List<WorkoutType> allTypes;

    public void loadSessionForEdit() throws IOException {
        allTypes = typeService.findAllTypes();

        if ("new".equals(sessionId)) {
            session = new WorkoutSession();
            session.setStatus(WorkoutStatus.PLANNED);
            session.setStartTime(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0));
            if (typeId != null) {
                typeService.findTypeById(UUID.fromString(typeId))
                        .ifPresent(session::setWorkoutType);
            }
        } else {
            if (sessionId != null) {
                session = sessionService.findSessionById(UUID.fromString(sessionId))
                        .orElse(new WorkoutSession());
                
                checkPermissions();
            }
        }
    }

    public void loadSessionForView() throws IOException {
        if (sessionId != null) {
            session = sessionService.findSessionById(UUID.fromString(sessionId))
                    .orElse(null);
            
            if (session != null) {
                checkPermissions();
            }
        }
    }

    private void checkPermissions() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        
        if (externalContext.isUserInRole("admin")) {
            return;
        }
        
        String remoteUser = externalContext.getRemoteUser();
        if (session.getUser() != null && !session.getUser().getUsername().equals(remoteUser)) {
            externalContext.responseSendError(403, "You are not authorized to view/edit this session");
            context.responseComplete();
        }
    }

    public String saveSession() {
        sessionService.saveWorkoutSession(session);
        return "category_view.xhtml?id=" + session.getWorkoutType().getId() + "&faces-redirect=true";
    }

    public WorkoutStatus[] getWorkoutStatuses() {
        return WorkoutStatus.values();
    }
}