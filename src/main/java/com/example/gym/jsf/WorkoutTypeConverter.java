package com.example.gym.jsf;

import com.example.gym.model.WorkoutType;
import com.example.gym.service.WorkoutService;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import java.util.UUID;

/**
 * Konwerter JSF dla encji WorkoutType.
 * Wymagany przez Lab 3  do obsługi rozwijanej listy.
 * Używa @FacesConverter(managed = true), aby umożliwić @Inject.
 */
@FacesConverter(value = "workoutTypeConverter", managed = true)
public class WorkoutTypeConverter implements Converter<WorkoutType> {

    @Inject
    private WorkoutService workoutService;

    @Override
    public WorkoutType getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            UUID id = UUID.fromString(value);
            return workoutService.findTypeById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, WorkoutType value) {
        if (value == null) {
            return "";
        }
        return value.getId().toString();
    }
}