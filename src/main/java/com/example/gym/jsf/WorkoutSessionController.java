package com.example.gym.jsf;

import com.example.gym.exception.OptimisticLockConflictException;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.service.WorkoutSessionService;
import com.example.gym.service.WorkoutTypeService;
import jakarta.ejb.EJBException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

    // Pola do obsługi konfliktu wersji
    @Getter
    private boolean versionConflict = false;

    @Getter
    private WorkoutSession databaseVersion;  // Aktualna wersja z bazy

    @Getter
    private WorkoutSession userVersion;      // Wersja z danymi użytkownika

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
        // Walidacja Bean Validation przed zapisem (walidator tworzony lokalnie)
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<WorkoutSession>> violations = validator.validate(session);
            if (!violations.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                for (ConstraintViolation<WorkoutSession> violation : violations) {
                    String fieldName = violation.getPropertyPath().toString();
                    String message = violation.getMessage();
                    context.addMessage(fieldName.isEmpty() ? null : fieldName,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
                }
                return null; // Pozostań na stronie z błędami
            }
        }

        try {
            versionConflict = false;
            sessionService.saveWorkoutSession(session);
            return "category_view.xhtml?id=" + session.getWorkoutType().getId() + "&faces-redirect=true";
        } catch (OptimisticLockConflictException e) {
            // Bezpośrednia obsługa konfliktu wersji
            handleVersionConflict(e);
            return null;
        } catch (EJBException e) {
            // EJB opakowuje wyjątki - sprawdź czy to nasz OptimisticLockConflictException
            Throwable cause = e.getCause();
            if (cause instanceof OptimisticLockConflictException) {
                handleVersionConflict((OptimisticLockConflictException) cause);
                return null;
            }
            // Inny błąd EJB - przekaż dalej
            throw e;
        }
    }

    /**
     * Obsługuje konflikt wersji - ustawia stan i wyświetla komunikat.
     */
    private void handleVersionConflict(OptimisticLockConflictException e) {
        versionConflict = true;
        databaseVersion = e.getDatabaseVersion();
        userVersion = e.getUserVersion();
        
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Konflikt wersji! / Version conflict!", 
                "Sesja została zmodyfikowana przez innego użytkownika. / Session was modified by another user."));
    }

    /**
     * Nadpisuje aktualną wersję w bazie danymi użytkownika (force save).
     */
    public String forceOverwrite() {
        if (userVersion != null && databaseVersion != null) {
            // Ustaw wersję z bazy danych, aby nadpisać
            session.setVersion(databaseVersion.getVersion());
            versionConflict = false;
            return saveSession();
        }
        return null;
    }

    /**
     * Odświeża formularz z danymi z bazy danych.
     */
    public String refreshFromDatabase() {
        if (databaseVersion != null) {
            session = databaseVersion;
            versionConflict = false;
            databaseVersion = null;
            userVersion = null;
        }
        return null;
    }

    public WorkoutStatus[] getWorkoutStatuses() {
        return WorkoutStatus.values();
    }
}