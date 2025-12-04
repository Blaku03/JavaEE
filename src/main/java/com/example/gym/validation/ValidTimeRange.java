package com.example.gym.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Własny walidator Bean Validation sprawdzający, czy czas zakończenia
 * jest po czasie rozpoczęcia (jeśli oba są podane).
 */
@Documented
@Constraint(validatedBy = ValidTimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimeRange {
    
    String message() default "End time must be after start time";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
