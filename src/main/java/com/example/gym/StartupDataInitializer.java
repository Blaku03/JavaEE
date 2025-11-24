package com.example.gym;

import com.example.gym.model.User;
import com.example.gym.model.WorkoutSession;
import com.example.gym.model.WorkoutType;
import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.service.PasswordHashService;
import com.example.gym.service.UserService;
import com.example.gym.service.WorkoutSessionService;
import com.example.gym.service.WorkoutTypeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class StartupDataInitializer {

    @Inject
    private WorkoutTypeService typeService;

    @Inject
    private WorkoutSessionService sessionService;

    @Inject
    private UserService userService;

    @Inject
    private PasswordHashService passwordHashService;

    @Transactional
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("==========================================================");
        System.out.println("StartupDataInitializer: Inicjalizacja danych JPA (Lab 6)...");
        System.out.println("==========================================================");

        try {
            // Tworzenie użytkowników
            User admin = User.builder()
                    .id(UUID.randomUUID())
                    .username("admin")
                    .password(passwordHashService.hashPassword("admin123"))
                    .email("admin@gym.com")
                    .role("admin")
                    .build();
            userService.createUser(admin);
            System.out.println("[Lab 6] Utworzono administratora: " + admin.getUsername());

            User user1 = User.builder()
                    .id(UUID.randomUUID())
                    .username("john")
                    .password(passwordHashService.hashPassword("john123"))
                    .email("john@example.com")
                    .role("user")
                    .build();
            userService.createUser(user1);
            System.out.println("[Lab 6] Utworzono użytkownika: " + user1.getUsername());

            User user2 = User.builder()
                    .id(UUID.randomUUID())
                    .username("jane")
                    .password(passwordHashService.hashPassword("jane123"))
                    .email("jane@example.com")
                    .role("user")
                    .build();
            userService.createUser(user2);
            System.out.println("[Lab 6] Utworzono użytkownika: " + user2.getUsername());

            // Tworzenie kategorii
            WorkoutType cardio = typeService.createWorkoutTypeSystem("Cardio", "Trening wytrzymałościowy");
            WorkoutType strength = typeService.createWorkoutTypeSystem("Trening Siłowy", "Budowanie masy mięśniowej");
            System.out.println("[Lab 6] Utworzono kategorie w bazie danych: " + cardio.getName() + ", " + strength.getName());

            // Tworzenie sesji przypisanych do użytkowników
            WorkoutSession session1 = WorkoutSession.builder()
                    .startTime(LocalDateTime.now().minusDays(1))
                    .endTime(LocalDateTime.now().minusDays(1).plusHours(1))
                    .status(WorkoutStatus.COMPLETED)
                    .workoutType(cardio)
                    .user(user1)
                    .build();
            sessionService.saveWorkoutSession(session1);
            System.out.println("[Lab 6] Dodano sesję Cardio dla użytkownika: " + user1.getUsername());

            WorkoutSession session2 = WorkoutSession.builder()
                    .startTime(LocalDateTime.now().plusDays(1))
                    .status(WorkoutStatus.PLANNED)
                    .workoutType(strength)
                    .user(user1)
                    .build();
            sessionService.saveWorkoutSession(session2);
            System.out.println("[Lab 6] Dodano sesję siłową dla użytkownika: " + user1.getUsername());

            WorkoutSession session3 = WorkoutSession.builder()
                    .startTime(LocalDateTime.now())
                    .status(WorkoutStatus.IN_PROGRESS)
                    .workoutType(cardio)
                    .user(user2)
                    .build();
            sessionService.saveWorkoutSession(session3);
            System.out.println("[Lab 6] Dodano sesję Cardio dla użytkownika: " + user2.getUsername());

            System.out.println("==========================================================");
            System.out.println("StartupDataInitializer: Demonstracja JPA zakończona.");
            System.out.println("==========================================================");

        } catch (Exception e) {
            System.err.println("[Lab 6] Wystąpił błąd podczas inicjalizacji danych startowych JPA.");
            e.printStackTrace();
        }
    }
}