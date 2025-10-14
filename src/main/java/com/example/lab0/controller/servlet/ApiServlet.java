package com.example.lab0.controller.servlet;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.lab0.user.controller.api.UserController;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = ApiServlet.Paths.API + "/*")
@MultipartConfig(maxFileSize = 200 * 1024)
public class ApiServlet extends HttpServlet {

    private UserController userController;

    private final Jsonb jsonb = JsonbBuilder.create();

    public static final class Paths {
        public static final String API = "/api";
    }

    public static final class Patterns {
        private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        public static final Pattern USERS = Pattern.compile("/users/?");
        public static final Pattern USER = Pattern.compile("/users/(%s)".formatted(UUID_PATTERN.pattern()));
        public static final Pattern USER_AVATAR = Pattern.compile("/users/(%s)/avatar".formatted(UUID_PATTERN.pattern()));
    }

    @Override
    public void init() throws ServletException {
        userController = (UserController) getServletContext().getAttribute("userController");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";

        if (path.matches(Patterns.USERS.pattern())) {
            response.setContentType("application/json");
            response.getWriter().write(jsonb.toJson(userController.getUsers()));
        } else if (path.matches(Patterns.USER.pattern())) {
            UUID uuid = extractUuid(Patterns.USER, path);
            Object user = userController.getUser(uuid);
            if (user != null) {
                response.setContentType("application/json");
                response.getWriter().write(jsonb.toJson(user));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (path.matches(Patterns.USER_AVATAR.pattern())) {
            UUID uuid = extractUuid(Patterns.USER_AVATAR, path);
            byte[] avatar = userController.getUserAvatar(uuid);
            if (avatar != null) {
                response.setContentType("image/png"); // Założenie, że to PNG
                response.setContentLength(avatar.length);
                response.getOutputStream().write(avatar);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";

        if (path.matches(Patterns.USER_AVATAR.pattern())) {
            UUID uuid = extractUuid(Patterns.USER_AVATAR, path);
            userController.putUserAvatar(uuid, request.getPart("avatar").getInputStream());
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";
        if (path.matches(Patterns.USER_AVATAR.pattern())) {
            UUID uuid = extractUuid(Patterns.USER_AVATAR, path);
            userController.deleteUserAvatar(uuid, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    private UUID extractUuid(Pattern pattern, String path) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            return UUID.fromString(matcher.group(1));
        }
        throw new IllegalArgumentException("No UUID in path.");
    }
}