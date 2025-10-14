package com.example.lab0.user.controller.api;

import com.example.lab0.user.dto.GetUserResponse;
import com.example.lab0.user.dto.GetUsersResponse;
import com.example.lab0.user.dto.function.UserToResponseFunction;
import com.example.lab0.user.dto.function.UsersToResponseFunction;
import com.example.lab0.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class UserController {
    private final UserService service;
    private final UserToResponseFunction userToResponse;
    private final UsersToResponseFunction usersToResponse;

    public UserController(UserService service) {
        this.service = service;
        this.userToResponse = new UserToResponseFunction();
        this.usersToResponse = new UsersToResponseFunction();
    }

    public GetUserResponse getUser(UUID id) {
        return service.find(id)
                .map(userToResponse)
                .orElse(null);
    }

    public GetUsersResponse getUsers() {
        return usersToResponse.apply(service.findAll());
    }

    public byte[] getUserAvatar(UUID id) throws IOException {
        return service.getAvatar(id).orElse(null);
    }

    public void putUserAvatar(UUID id, InputStream avatarStream) throws IOException {
        service.storeAvatar(id, avatarStream);
    }

    public void deleteUserAvatar(UUID id, HttpServletResponse response) throws IOException {
        if (service.getAvatar(id).isPresent()) {
            service.deleteAvatar(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}