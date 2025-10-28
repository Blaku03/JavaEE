package com.example.gym.jsf;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.service.WorkoutService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Bean CDI obsługujący widoki listy kategorii (categories.xhtml)
 * oraz szczegółów kategorii (category_view.xhtml).
 * Realizuje zadanie 2.
 */
@Named
@ViewScoped
public class WorkoutTypeController implements Serializable {

    @Inject
    private WorkoutService workoutService;

    @Getter
    private List<WorkoutType> workoutTypes;

    @Getter @Setter
    private String selectedTypeId; // <-- ZMIANA: z UUID na String

    @Getter
    private WorkoutType selectedWorkoutType;

    @Getter
    private List<WorkoutSession> sessionsForType;

    @PostConstruct
    public void init() {
        // Ładuje listę wszystkich kategorii
        workoutTypes = workoutService.findAllTypes();
    }

    /**
     * Ładuje szczegóły wybranej kategorii i jej sesje.
     * Wywoływane przez f:viewAction w category_view.xhtml.
     */
    public void loadSelectedCategory() {
        // ZMIANA: Dodajemy ręczną konwersję String -> UUID
        if (selectedTypeId != null && !selectedTypeId.isEmpty()) {
            try {
                UUID uuid = UUID.fromString(selectedTypeId); // Konwersja
                selectedWorkoutType = workoutService.findTypeById(uuid).orElse(null);

                if (selectedWorkoutType != null) {
                    // Ładuje listę elementów (sesji) dla kategorii
                    sessionsForType = workoutService.findSessionsByTypeId(selectedWorkoutType.getId());
                }
            } catch (IllegalArgumentException e) {
                // Obsługa błędu, jeśli ID w URL nie jest poprawnym UUID
                System.err.println("Otrzymano nieprawidłowy format UUID: " + selectedTypeId);
                selectedWorkoutType = null;
            }
        }
    }

    /**
     * Akcja usuwania kategorii.
     */
    public String deleteCategory(UUID id) {
        workoutService.deleteWorkoutType(id);
        return "categories.xhtml?faces-redirect=true"; // Przeładuj stronę
    }

    /**
     * Akcja usuwania elementu (sesji) z widoku kategorii.
     */
    public String deleteSession(UUID sessionId, UUID typeId) {
        workoutService.deleteWorkoutSession(sessionId);
        // Przeładuj bieżący widok kategorii
        return "category_view.xhtml?id=" + typeId + "&faces-redirect=true";
    }
}