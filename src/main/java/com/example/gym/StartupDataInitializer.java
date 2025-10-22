package com.example.gym;

import com.example.gym.model.User;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.WorkoutService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class StartupDataInitializer {

    @Inject
    private WorkoutService workoutService;

    @Inject
    private UserRepository userRepository;

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("==========================================================");
        System.out.println("StartupDataInitializer: Demonstrowanie serwisów (Lab 2)...");
        System.out.println("==========================================================");

        try {
            WorkoutType cardio = workoutService.createWorkoutType("Cardio", "Trening wytrzymałościowy");
            WorkoutType strength = workoutService.createWorkoutType("Trening Siłowy", "Budowanie masy mięśniowej");
            System.out.println("[Lab 2 Demo] Utworzono kategorie: " + cardio.getName() + ", " + strength.getName());

            User testUser = userRepository.findById(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3452")).orElse(null);

            if (testUser != null) {
                System.out.println("[Lab 2 Demo] Znaleziono użytkownika testowego: " + testUser.getName());

                WorkoutSession session1 = WorkoutSession.builder()
                        .startTime(LocalDateTime.now().minusDays(1))
                        .endTime(LocalDateTime.now().minusDays(1).plusHours(1))
                        .status(WorkoutStatus.COMPLETED)
                        .build();

                workoutService.createWorkoutSession(testUser.getId(), cardio.getId(), session1);
                System.out.println("[Lab 2 Demo] Dodano 'Ukończoną' sesję Cardio dla: " + testUser.getName());

                WorkoutSession session2 = WorkoutSession.builder()
                        .startTime(LocalDateTime.now().plusDays(1))
                        .status(WorkoutStatus.PLANNED)
                        .build();

                workoutService.createWorkoutSession(testUser.getId(), strength.getId(), session2);
                System.out.println("[Lab 2 Demo] Dodano 'Zaplanowaną' sesję siłową dla: " + testUser.getName());

                User updatedUser = userRepository.findById(testUser.getId()).get();
                System.out.println("[Lab 2 Demo] Użytkownik " + updatedUser.getName() + " ma teraz " + updatedUser.getWorkoutSessions().size() + " sesje treningowe.");

            } else {
                System.err.println("[Lab 2 Demo] Nie znaleziono testowego użytkownika do demonstracji.");
            }

            System.out.println("==========================================================");
            System.out.println("StartupDataInitializer: Demonstracja zakończona.");
            System.out.println("==========================================================");

        } catch (Exception e) {
            System.err.println("[Lab 2 Demo] Wystąpił błąd podczas inicjalizacji danych startowych.");
            e.printStackTrace();
        }
    }
}