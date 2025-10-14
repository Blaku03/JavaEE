package com.example.lab0.workouttype.controller.api;

import com.example.lab0.workoutsession.dto.GetWorkoutSessionsResponse;
import com.example.lab0.workoutsession.dto.function.WorkoutSessionsToResponseFunction;
import com.example.lab0.workoutsession.service.WorkoutSessionService;
import com.example.lab0.workouttype.dto.GetWorkoutTypeResponse;
import com.example.lab0.workouttype.dto.GetWorkoutTypesResponse;
import com.example.lab0.workouttype.dto.function.WorkoutTypeToResponseFunction;
import com.example.lab0.workouttype.dto.function.WorkoutTypesToResponseFunction;
import com.example.lab0.workouttype.service.WorkoutTypeService;

import java.util.UUID;

public class WorkoutTypeController {

    private final WorkoutTypeService typeService;
    private final WorkoutSessionService sessionService;
    private final WorkoutTypeToResponseFunction typeToResponse;
    private final WorkoutTypesToResponseFunction typesToResponse;
    private final WorkoutSessionsToResponseFunction sessionsToResponse;


    public WorkoutTypeController(WorkoutTypeService typeService, WorkoutSessionService sessionService) {
        this.typeService = typeService;
        this.sessionService = sessionService;
        this.typeToResponse = new WorkoutTypeToResponseFunction();
        this.typesToResponse = new WorkoutTypesToResponseFunction();
        this.sessionsToResponse = new WorkoutSessionsToResponseFunction();
    }

    public GetWorkoutTypesResponse getWorkoutTypes() {
        return typesToResponse.apply(typeService.findAll());
    }

    public GetWorkoutSessionsResponse getWorkoutTypeSessions(UUID id) {
        return sessionsToResponse.apply(sessionService.findByWorkoutType(id));
    }
}