package com.example.gym.controller;

import com.example.gym.datastore.DataStore;
import com.example.gym.model.User;
import com.example.gym.repository.AvatarRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.AvatarService;
import com.example.gym.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/api/avatars/*")
@MultipartConfig
public class AvatarServlet extends HttpServlet {

    private AvatarService avatarService;
    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DataStore dataStore = (DataStore) getServletContext().getAttribute("dataStore");
        UserRepository userRepository = new UserRepository(dataStore);
        this.avatarService = new AvatarService(new AvatarRepository());
        this.userService = new UserService(userRepository, this.avatarService); // Poprawione tworzenie UserService
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            String userIdString = pathInfo.substring(1);
            UUID userId = UUID.fromString(userIdString);

            Path avatarPath = avatarService.getAvatar(userId);

            if (avatarPath != null && Files.exists(avatarPath)) {
                resp.setContentType("image/png");
                Files.copy(avatarPath, resp.getOutputStream());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Avatar not found");
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            String userIdString = pathInfo.substring(1);
            UUID userId = UUID.fromString(userIdString);

            // Poprawiona obsługa Optional
            Optional<User> userOptional = userService.findById(userId);
            if (userOptional.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            Part filePart = req.getPart("avatar");
            if (filePart == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'avatar' part");
                return;
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                avatarService.saveAvatar(userId, fileContent);
                resp.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            String userIdString = pathInfo.substring(1);
            UUID userId = UUID.fromString(userIdString);

            // Poprawiona obsługa Optional
            Optional<User> userOptional = userService.findById(userId);
            if (userOptional.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            avatarService.deleteAvatar(userId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid UUID format");
        }
    }
}