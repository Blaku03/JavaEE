package com.example.gym.exception;

import com.example.gym.model.WorkoutSession;
import lombok.Getter;

/**
 * Wyjątek rzucany przy wykryciu konfliktu wersji (optimistic locking).
 * Zawiera zarówno dane z bazy, jak i dane wprowadzone przez użytkownika.
 */
@Getter
public class OptimisticLockConflictException extends RuntimeException {

    private final WorkoutSession databaseVersion;  // Aktualna wersja z bazy
    private final WorkoutSession userVersion;      // Wersja z danymi użytkownika

    public OptimisticLockConflictException(String message, WorkoutSession databaseVersion, WorkoutSession userVersion) {
        super(message);
        this.databaseVersion = databaseVersion;
        this.userVersion = userVersion;
    }
}
