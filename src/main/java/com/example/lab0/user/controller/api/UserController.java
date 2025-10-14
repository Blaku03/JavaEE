package com.example.lab0.user.controller.api;

import com.example.lab0.user.dto.GetUserResponse;
import com.example.lab0.user.dto.GetUsersResponse;
import com.example.lab0.user.dto.function.UserToResponseFunction;
import com.example.lab0.user.dto.function.UsersToResponseFunction;
import com.example.lab0.user.service.UserService;
import com.example.lab0.workoutsession.dto.GetWorkoutSessionsResponse;
import com.example.lab0.workoutsession.dto.function.WorkoutSessionsToResponseFunction;
import com.example.lab0.workoutsession.service.WorkoutSessionService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class UserController {
    private final UserService userService;
    private final WorkoutSessionService sessionService;
    private final UserToResponseFunction userToResponse;
    private final UsersToResponseFunction usersToResponse;
    private final WorkoutSessionsToResponseFunction sessionsToResponse;


    public UserController(UserService userService, WorkoutSessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.userToResponse = new UserToResponseFunction();
        this.usersToResponse = new UsersToResponseFunction();
        this.sessionsToResponse = new WorkoutSessionsToResponseFunction();
    }

    public GetUserResponse getUser(UUID id) {
        return userService.find(id)
                .map(userToResponse)
                .orElse(null);
    }

    public GetUsersResponse getUsers() {
        return usersToResponse.apply(userService.findAll());
    }

    public byte[] getUserAvatar(UUID id) throws IOException {
        return userService.getAvatar(id).orElse(null);
    }

    public void putUserAvatar(UUID id, InputStream avatarStream) throws IOException {
        userService.storeAvatar(id, avatarStream);
    }

    public void deleteUserAvatar(UUID id, HttpServletResponse response) throws IOException {
        if (userService.getAvatar(id).isPresent()) {
            userService.deleteAvatar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public GetWorkoutSessionsResponse getUserWorkoutSessions(UUID id){
        return sessionsToResponse.apply(sessionService.findByUser(id));
    }
}