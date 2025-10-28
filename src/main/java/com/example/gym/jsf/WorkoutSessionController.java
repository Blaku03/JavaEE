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

/**
 * Bean CDI obsługujący widok szczegółów sesji (session_view.xhtml)
 * oraz formularz dodawania/edycji sesji (session_edit.xhtml).
 */
@Named
@ViewScoped
public class WorkoutSessionController implements Serializable {

    @Inject
    private WorkoutService workoutService;

    @Getter @Setter
    private String sessionId; // Parametr ID z URL

    @Getter @Setter
    private String typeId; // Parametr typeId z URL (dla nowych sesji)

    @Getter
    private WorkoutSession session;

    @Getter
    private List<WorkoutType> allTypes; // Dla listy rozwijanej [cite: 82]

    /**
     * Ładuje dane dla formularza edycji lub tworzy nowy obiekt.
     * Wywoływane przez f:viewAction w session_edit.xhtml.
     */
    public void loadSessionForEdit() {
        allTypes = workoutService.findAllTypes(); // Zawsze ładuj typy dla formularza

        if ("new".equals(sessionId)) {
            // Tworzenie nowego elementu
            session = new WorkoutSession();
            session.setStatus(WorkoutStatus.PLANNED); // Domyślna wartość
            session.setStartTime(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0));
            // Ustaw domyślną kategorię, jeśli przekazano typeId
            if (typeId != null) {
                workoutService.findTypeById(UUID.fromString(typeId))
                        .ifPresent(session::setWorkoutType);
            }
        } else {
            // Edycja istniejącego elementu
            if (sessionId != null) {
                session = workoutService.findSessionById(UUID.fromString(sessionId))
                        .orElse(new WorkoutSession()); // Obsługa błędu
            }
        }
    }

    /**
     * Ładuje sesję tylko do odczytu (dla session_view.xhtml).
     * Realizuje zadanie 3.
     */
    public void loadSessionForView() {
        if (sessionId != null) {
            session = workoutService.findSessionById(UUID.fromString(sessionId))
                    .orElse(null);
        }
    }

    /**
     * Akcja zapisu (nowego lub edytowanego) elementu.
     */
    public String saveSession() {
        workoutService.saveWorkoutSession(session);
        // Powrót do widoku kategorii, do której należy sesja
        return "category_view.xhtml?id=" + session.getWorkoutType().getId() + "&faces-redirect=true";
    }

    /**
     * Zwraca listę wszystkich statusów (dla formularza).
     */
    public WorkoutStatus[] getWorkoutStatuses() {
        return WorkoutStatus.values();
    }
}