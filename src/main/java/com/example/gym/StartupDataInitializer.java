package com.example.gym;

import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.service.WorkoutService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class StartupDataInitializer {

    @Inject
    private WorkoutService workoutService;

    @Transactional
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("==========================================================");
        System.out.println("StartupDataInitializer: Inicjalizacja danych JPA (Lab 5)...");
        System.out.println("==========================================================");

        try {
            WorkoutType cardio = workoutService.createWorkoutType("Cardio", "Trening wytrzymałościowy");
            WorkoutType strength = workoutService.createWorkoutType("Trening Siłowy", "Budowanie masy mięśniowej");
            System.out.println("[Lab 5 Demo] Utworzono kategorie w bazie danych: " + cardio.getName() + ", " + strength.getName());

            WorkoutSession session1 = WorkoutSession.builder()
                    .startTime(LocalDateTime.now().minusDays(1))
                    .endTime(LocalDateTime.now().minusDays(1).plusHours(1))
                    .status(WorkoutStatus.COMPLETED)
                    .workoutType(cardio)
                    .build();

            workoutService.saveWorkoutSession(session1);
            System.out.println("[Lab 5 Demo] Dodano 'Ukończoną' sesję Cardio do bazy.");

            WorkoutSession session2 = WorkoutSession.builder()
                    .startTime(LocalDateTime.now().plusDays(1))
                    .status(WorkoutStatus.PLANNED)
                    .workoutType(strength)
                    .build();

            workoutService.saveWorkoutSession(session2);
            System.out.println("[Lab 5 Demo] Dodano 'Zaplanowaną' sesję siłową do bazy.");


            System.out.println("==========================================================");
            System.out.println("StartupDataInitializer: Demonstracja JPA zakończona.");
            System.out.println("==========================================================");

        } catch (Exception e) {
            System.err.println("[Lab 5 Demo] Wystąpił błąd podczas inicjalizacji danych startowych JPA.");
            e.printStackTrace();
        }
    }
}