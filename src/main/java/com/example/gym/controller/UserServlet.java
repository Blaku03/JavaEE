package com.example.gym.controller;

import com.example.gym.dto.CreateUserRequest;
import com.example.gym.dto.GetUserResponse;
import com.example.gym.dto.GetUsersResponse;
import com.example.gym.dto.UpdateUserRequest;
import com.example.gym.model.User;
import com.example.gym.repository.AvatarRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.AvatarService;
import com.example.gym.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private UserService userService;
    private final Gson gson = new Gson();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        UserRepository userRepository = UserRepository.getInstance();
        AvatarService avatarService = new AvatarService(new AvatarRepository());
        this.userService = new UserService(userRepository, avatarService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetAllUsers(resp);
        } else {
            handleGetUserById(resp, pathInfo);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            CreateUserRequest createUserRequest = gson.fromJson(requestBody, CreateUserRequest.class);

            if (createUserRequest.getName() == null || createUserRequest.getEmail() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name and email are required.");
                return;
            }

            User newUser = userService.createUser(createUserRequest);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(GetUserResponse.builder()
                    .id(newUser.getId())
                    .name(newUser.getName())
                    .email(newUser.getEmail())
                    .build()));
        } catch (JsonSyntaxException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required in the path.");
            return;
        }

        try {
            String idString = pathInfo.substring(1);
            UUID id = UUID.fromString(idString);

            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            UpdateUserRequest updateUserRequest = gson.fromJson(requestBody, UpdateUserRequest.class);

            if (updateUserRequest.getName() == null || updateUserRequest.getEmail() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name and email are required.");
                return;
            }

            Optional<User> updatedUser = userService.updateUser(id, updateUserRequest);

            if (updatedUser.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
        } catch (JsonSyntaxException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required in the path.");
            return;
        }

        try {
            String idString = pathInfo.substring(1);
            UUID id = UUID.fromString(idString);

            if (userService.deleteUser(id)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
        }
    }


    private void handleGetAllUsers(HttpServletResponse resp) throws IOException {
        Collection<User> users = userService.findAll();
        List<GetUsersResponse.User> userDtos = users.stream()
                .map(user -> GetUsersResponse.User.builder().id(user.getId()).name(user.getName()).build())
                .collect(Collectors.toList());
        resp.getWriter().write(gson.toJson(new GetUsersResponse(userDtos)));
    }

    private void handleGetUserById(HttpServletResponse resp, String pathInfo) throws IOException {
        try {
            String idString = pathInfo.substring(1);
            UUID id = UUID.fromString(idString);
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                GetUserResponse userDto = GetUserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build();
                resp.getWriter().write(gson.toJson(userDto));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
        }
    }
}