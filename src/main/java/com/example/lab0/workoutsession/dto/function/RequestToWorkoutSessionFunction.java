package com.example.lab0.workoutsession.dto.function;

import com.example.lab0.User;
import com.example.lab0.WorkoutSession;
import com.example.lab0.WorkoutStatus;
import com.example.lab0.WorkoutType;
import com.example.lab0.workoutsession.dto.PutWorkoutSessionRequest;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

public class RequestToWorkoutSessionFunction implements BiFunction<PutWorkoutSessionRequest, User, WorkoutSession> {

    @Override
    public WorkoutSession apply(PutWorkoutSessionRequest request, User user) {
        return WorkoutSession.builder()
                .name(request.getName())
                .startTime(LocalDateTime.now())
                .status(WorkoutStatus.PLANNED)
                .user(user)
                .workoutType(WorkoutType.builder().id(request.getWorkoutType()).build())
                .build();
    }
}