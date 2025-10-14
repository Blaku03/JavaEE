package com.example.lab0.configuration;

import com.example.lab0.User;
import com.example.lab0.WorkoutSession;
import com.example.lab0.WorkoutStatus;
import com.example.lab0.WorkoutType;
import com.example.lab0.datastore.DataStore;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

@WebListener
public class InitializedData implements ServletContextListener {

    private static final Logger log = Logger.getLogger(InitializedData.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataStore dataStore = (DataStore) sce.getServletContext().getAttribute("dataStore");
        init(dataStore);
    }

    private void init(DataStore store) {
        // --- Workout Types ---
        WorkoutType strength = WorkoutType.builder()
                .id(UUID.fromString("f5875513-bf7b-4ae1-b8a5-5b70a1b90e76"))
                .name("Strength")
                .description("Build muscle and increase raw power.")
                .intensityLevel(8)
                .sessions(new ArrayList<>()) // Inicjalizuj pustą listę
                .build();
        WorkoutType cardio = WorkoutType.builder()
                .id(UUID.fromString("b8387964-054d-4874-9177-3e143681423c"))
                .name("Cardio")
                .description("Improve cardiovascular health and endurance.")
                .intensityLevel(6)
                .sessions(new ArrayList<>()) // Inicjalizuj pustą listę
                .build();
        store.createWorkoutType(strength);
        store.createWorkoutType(cardio);

        // --- Users ---
        User user1 = User.builder()
                .id(UUID.fromString("c4804e0f-769e-4ab9-9ebe-0578fb4f00a1"))
                .username("john_doe")
                .email("john.doe@example.com")
                .registrationDate(LocalDate.of(2023, 1, 15))
                .sessions(new ArrayList<>()) // Inicjalizuj pustą listę
                .build();
        User user2 = User.builder()
                .id(UUID.fromString("81e1c2a9-7f57-439b-b53d-6db88b071e4e"))
                .username("jane_smith")
                .email("jane.smith@example.com")
                .registrationDate(LocalDate.of(2023, 3, 22))
                .sessions(new ArrayList<>()) // Inicjalizuj pustą listę
                .build();
        store.createUser(user1);
        store.createUser(user2);

        // --- Workout Sessions ---
        WorkoutSession session1 = WorkoutSession.builder()
                .id(UUID.fromString("525d3e7b-bb1f-4c13-bf17-926d1a12e4c0"))
                .name("Morning Run")
                .startTime(LocalDateTime.now().minusDays(1))
                .status(WorkoutStatus.COMPLETED)
                .user(user1)
                .workoutType(cardio)
                .build();

        WorkoutSession session2 = WorkoutSession.builder()
                .id(UUID.fromString("3c6f1eb1-8069-44ab-988f-d7fed2b65d87"))
                .name("Chest Day")
                .startTime(LocalDateTime.now())
                .status(WorkoutStatus.PLANNED)
                .user(user2)
                .workoutType(strength)
                .build();

        store.createWorkoutSession(session1);
        store.createWorkoutSession(session2);
    }
}