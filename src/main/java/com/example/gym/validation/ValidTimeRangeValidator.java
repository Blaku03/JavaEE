package com.example.gym.validation;

import com.example.gym.model.WorkoutSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementacja walidatora @ValidTimeRange.
 * Sprawdza, czy endTime jest po startTime (jeśli oba są ustawione).
 */
public class ValidTimeRangeValidator implements ConstraintValidator<ValidTimeRange, WorkoutSession> {

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        // Nie wymaga inicjalizacji
    }

    @Override
    public boolean isValid(WorkoutSession session, ConstraintValidatorContext context) {
        // Null session is handled by other validators
        if (session == null) {
            return true;
        }

        // Jeśli endTime nie jest ustawione, walidacja przechodzi
        if (session.getEndTime() == null) {
            return true;
        }

        // Jeśli startTime nie jest ustawione, nie możemy porównać - walidacja przechodzi
        // (ale @NotNull na startTime złapie ten przypadek)
        if (session.getStartTime() == null) {
            return true;
        }

        // Sprawdź, czy endTime jest po startTime
        boolean isValid = session.getEndTime().isAfter(session.getStartTime());

        if (!isValid) {
            // Dodaj komunikat błędu do pola endTime
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("endTime")
                   .addConstraintViolation();
        }

        return isValid;
    }
}
