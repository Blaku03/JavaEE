package com.example.lab0.controller.servlet;

import com.example.lab0.user.controller.api.UserController;
import com.example.lab0.workoutsession.controller.api.WorkoutSessionController;
import com.example.lab0.workoutsession.dto.PatchWorkoutSessionRequest;
import com.example.lab0.workoutsession.dto.PutWorkoutSessionRequest;
import com.example.lab0.workouttype.controller.api.WorkoutTypeController;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = ApiServlet.Paths.API + "/*")
@MultipartConfig(maxFileSize = 200 * 1024)
public class ApiServlet extends HttpServlet {

    private UserController userController;
    private WorkoutSessionController workoutSessionController;
    private WorkoutTypeController workoutTypeController;


    private final Jsonb jsonb = JsonbBuilder.create();

    public static final class Paths {
        public static final String API = "/api";
    }

    public static final class Patterns {
        private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

        public static final Pattern USERS = Pattern.compile("/users/?");
        public static final Pattern USER = Pattern.compile("/users/(%s)".formatted(UUID_PATTERN.pattern()));
        public static final Pattern USER_AVATAR = Pattern.compile("/users/(%s)/avatar".formatted(UUID_PATTERN.pattern()));
        public static final Pattern USER_SESSIONS = Pattern.compile("/users/(%s)/workout-sessions".formatted(UUID_PATTERN.pattern()));

        public static final Pattern WORKOUT_SESSIONS = Pattern.compile("/workout-sessions/?");
        public static final Pattern WORKOUT_SESSION = Pattern.compile("/workout-sessions/(%s)".formatted(UUID_PATTERN.pattern()));
        public static final Pattern WORKOUT_SESSION_IMAGE = Pattern.compile("/workout-sessions/(%s)/image".formatted(UUID_PATTERN.pattern()));

        public static final Pattern WORKOUT_TYPES = Pattern.compile("/workout-types/?");
        public static final Pattern WORKOUT_TYPE_SESSIONS = Pattern.compile("/workout-types/(%s)/workout-sessions".formatted(UUID_PATTERN.pattern()));
    }

    @Override
    public void init() throws ServletException {
        userController = (UserController) getServletContext().getAttribute("userController");
        workoutSessionController = (WorkoutSessionController) getServletContext().getAttribute("workoutSessionController");
        workoutTypeController = (WorkoutTypeController) getServletContext().getAttribute("workoutTypeController");
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
                response.setContentType("image/png");
                response.setContentLength(avatar.length);
                response.getOutputStream().write(avatar);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (path.matches(Patterns.USER_SESSIONS.pattern())) {
            UUID uuid = extractUuid(Patterns.USER_SESSIONS, path);
            response.setContentType("application/json");
            response.getWriter().write(jsonb.toJson(userController.getUserWorkoutSessions(uuid)));
        } else if (path.matches(Patterns.WORKOUT_SESSION.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_SESSION, path);
            Object session = workoutSessionController.getWorkoutSession(uuid);
            if (session != null) {
                response.setContentType("application/json");
                response.getWriter().write(jsonb.toJson(session));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (path.matches(Patterns.WORKOUT_SESSION_IMAGE.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_SESSION_IMAGE, path);
            byte[] image = workoutSessionController.getWorkoutSessionImage(uuid);
            if (image != null) {
                response.setContentType("image/png");
                response.setContentLength(image.length);
                response.getOutputStream().write(image);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if (path.matches(Patterns.WORKOUT_TYPES.pattern())) {
            response.setContentType("application/json");
            response.getWriter().write(jsonb.toJson(workoutTypeController.getWorkoutTypes()));
        } else if (path.matches(Patterns.WORKOUT_TYPE_SESSIONS.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_TYPE_SESSIONS, path);
            response.setContentType("application/json");
            response.getWriter().write(jsonb.toJson(workoutTypeController.getWorkoutTypeSessions(uuid)));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";

        if (path.matches(Patterns.USER_AVATAR.pattern())) {
            UUID uuid = extractUuid(Patterns.USER_AVATAR, path);
            Optional<Part> avatarPart = request.getParts().stream().filter(part -> part.getName().equals("avatar")).findFirst();
            if (avatarPart.isPresent()) {
                userController.putUserAvatar(uuid, avatarPart.get().getInputStream());
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else if (path.matches(Patterns.WORKOUT_SESSION.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_SESSION, path);
            String body = request.getReader().lines().collect(Collectors.joining());
            PutWorkoutSessionRequest req = jsonb.fromJson(body, PutWorkoutSessionRequest.class);
            workoutSessionController.createWorkoutSession(uuid, req, response);
        } else if (path.matches(Patterns.WORKOUT_SESSION_IMAGE.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_SESSION_IMAGE, path);
            Optional<Part> imagePart = request.getParts().stream().filter(part -> part.getName().equals("image")).findFirst();
            if(imagePart.isPresent()){
                workoutSessionController.putWorkoutSessionImage(uuid, imagePart.get().getInputStream(), response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";
        if (path.matches(Patterns.WORKOUT_SESSION.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_SESSION, path);
            String body = request.getReader().lines().collect(Collectors.joining());
            PatchWorkoutSessionRequest req = jsonb.fromJson(body, PatchWorkoutSessionRequest.class);
            workoutSessionController.updateWorkoutSession(uuid, req, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";
        if (path.matches(Patterns.WORKOUT_SESSION.pattern())) {
            UUID uuid = extractUuid(Patterns.WORKOUT_SESSION, path);
            workoutSessionController.deleteWorkoutSession(uuid, response);
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