package com.example.lab0.workoutsession.dto.function;

import com.example.lab0.WorkoutSession;
import com.example.lab0.workoutsession.dto.PatchWorkoutSessionRequest;
import java.util.function.BiFunction;

public class UpdateWorkoutSessionWithRequestFunction implements BiFunction<WorkoutSession, PatchWorkoutSessionRequest, WorkoutSession> {
    @Override
    public WorkoutSession apply(WorkoutSession session, PatchWorkoutSessionRequest request) {
        session.setName(request.getName());
        return session;
    }
}