package com.example.lab0.datastore;

import com.example.lab0.User;
import com.example.lab0.WorkoutSession;
import com.example.lab0.WorkoutType;
import lombok.extern.java.Log;

import java.util.*;
import java.util.stream.Collectors;

@Log
public class DataStore {

    private final Set<User> users = new HashSet<>();
    private final Set<WorkoutSession> workoutSessions = new HashSet<>();
    private final Set<WorkoutType> workoutTypes = new HashSet<>();

    // --- User methods ---
    public synchronized List<User> findAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized void createUser(User user) {
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new IllegalArgumentException("User with id " + user.getId() + " already exists");
        }
        users.add(user);
    }

    public synchronized Optional<User> findUser(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    // --- WorkoutType methods ---
    public synchronized List<WorkoutType> findAllWorkoutTypes() {
        return new ArrayList<>(workoutTypes);
    }

    public synchronized Optional<WorkoutType> findWorkoutType(UUID id) {
        return workoutTypes.stream().filter(type -> type.getId().equals(id)).findFirst();
    }

    public synchronized void createWorkoutType(WorkoutType workoutType) {
        workoutTypes.add(workoutType);
    }

    // --- WorkoutSession methods ---
    public synchronized List<WorkoutSession> findAllWorkoutSessions() {
        return new ArrayList<>(workoutSessions);
    }

    public synchronized Optional<WorkoutSession> findWorkoutSession(UUID id) {
        return workoutSessions.stream().filter(session -> session.getId().equals(id)).findFirst();
    }

    public synchronized void createWorkoutSession(WorkoutSession session) {
        // Dodaj sesję do ogólnej listy
        workoutSessions.add(session);

        // Zaktualizuj relacje dwukierunkowe
        session.getUser().getSessions().add(session);
        session.getWorkoutType().getSessions().add(session);
    }

    public synchronized void deleteWorkoutSession(UUID id) {
        findWorkoutSession(id).ifPresent(session -> {
            workoutSessions.remove(session);
            // Usuń sesję z powiązanych obiektów
            if (session.getUser() != null && session.getUser().getSessions() != null) {
                session.getUser().getSessions().remove(session);
            }
            if (session.getWorkoutType() != null && session.getWorkoutType().getSessions() != null) {
                session.getWorkoutType().getSessions().remove(session);
            }
        });
    }

    public synchronized void updateWorkoutSession(WorkoutSession session) {
        findWorkoutSession(session.getId()).ifPresent(original -> {
            workoutSessions.remove(original);
            workoutSessions.add(session);
        });
    }
}