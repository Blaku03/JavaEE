package com.example.lab0.workoutsession.controller.api;

import com.example.lab0.User;
import com.example.lab0.WorkoutSession;
import com.example.lab0.user.service.UserService;
import com.example.lab0.workoutsession.dto.GetWorkoutSessionResponse;
import com.example.lab0.workoutsession.dto.PatchWorkoutSessionRequest;
import com.example.lab0.workoutsession.dto.PutWorkoutSessionRequest;
import com.example.lab0.workoutsession.dto.function.RequestToWorkoutSessionFunction;
import com.example.lab0.workoutsession.dto.function.UpdateWorkoutSessionWithRequestFunction;
import com.example.lab0.workoutsession.dto.function.WorkoutSessionToResponseFunction;
import com.example.lab0.workoutsession.service.WorkoutSessionService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

public class WorkoutSessionController {
    private final WorkoutSessionService sessionService;
    private final UserService userService;
    private final WorkoutSessionToResponseFunction sessionToResponse;
    private final RequestToWorkoutSessionFunction requestToSession;
    private final UpdateWorkoutSessionWithRequestFunction updateRequestToSession;

    public WorkoutSessionController(WorkoutSessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.sessionToResponse = new WorkoutSessionToResponseFunction();
        this.requestToSession = new RequestToWorkoutSessionFunction();
        this.updateRequestToSession = new UpdateWorkoutSessionWithRequestFunction();
    }

    public GetWorkoutSessionResponse getWorkoutSession(UUID id) {
        return sessionService.find(id).map(sessionToResponse).orElse(null);
    }

    public void createWorkoutSession(UUID id, PutWorkoutSessionRequest request, HttpServletResponse response) throws IOException {
        Optional<User> user = userService.find(request.getUser());
        if (user.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        WorkoutSession newSession = requestToSession.apply(request, user.get());
        newSession.setId(id);
        sessionService.create(newSession);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    public void updateWorkoutSession(UUID id, PatchWorkoutSessionRequest request, HttpServletResponse response) throws IOException {
        Optional<WorkoutSession> session = sessionService.find(id);
        if (session.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        WorkoutSession updatedSession = updateRequestToSession.apply(session.get(), request);
        sessionService.update(updatedSession);
    }

    public void deleteWorkoutSession(UUID id, HttpServletResponse response) throws IOException {
        if (sessionService.find(id).isPresent()) {
            sessionService.delete(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public byte[] getWorkoutSessionImage(UUID id) throws IOException {
        return sessionService.getImage(id).orElse(null);
    }

    public void putWorkoutSessionImage(UUID id, InputStream imageStream, HttpServletResponse response) throws IOException {
        if(sessionService.find(id).isEmpty()){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        sessionService.storeImage(id, imageStream);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}