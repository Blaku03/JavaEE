package com.example.lab0.workoutsession.service;

import com.example.lab0.User;
import com.example.lab0.WorkoutSession;
import com.example.lab0.user.repository.api.UserRepository;
import com.example.lab0.workoutsession.repository.api.WorkoutSessionRepository;
import com.example.lab0.workouttype.repository.api.WorkoutTypeRepository;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final WorkoutTypeRepository typeRepository;
    private final Path imageDirectory;

    public WorkoutSessionService(WorkoutSessionRepository sessionRepository, UserRepository userRepository,
                                 WorkoutTypeRepository typeRepository, ServletContext context) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.typeRepository = typeRepository;
        String imageDir = context.getInitParameter("image.directory");
        this.imageDirectory = Paths.get(imageDir);
        if (!Files.exists(imageDirectory)) {
            try {
                Files.createDirectories(imageDirectory);
            } catch (IOException e) {
                throw new IllegalStateException("Could not create image directory", e);
            }
        }
    }

    public List<WorkoutSession> findAll() {
        return sessionRepository.findAll();
    }

    public Optional<WorkoutSession> find(UUID id) {
        return sessionRepository.find(id);
    }

    public List<WorkoutSession> findByUser(UUID userId) {
        return sessionRepository.findAll().stream()
                .filter(session -> session.getUser() != null && session.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<WorkoutSession> findByWorkoutType(UUID typeId) {
        return sessionRepository.findAll().stream()
                .filter(session -> session.getWorkoutType() != null && session.getWorkoutType().getId().equals(typeId))
                .collect(Collectors.toList());
    }

    public void create(WorkoutSession session) {
        sessionRepository.create(session);
    }

    public void update(WorkoutSession session) {
        sessionRepository.update(session);
    }

    public void delete(UUID id) {
        sessionRepository.delete(id);
    }

    public void storeImage(UUID sessionId, InputStream inputStream) throws IOException {
        Path imagePath = imageDirectory.resolve(sessionId.toString());
        Files.copy(inputStream, imagePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public Optional<byte[]> getImage(UUID sessionId) throws IOException {
        Path imagePath = imageDirectory.resolve(sessionId.toString());
        if (Files.exists(imagePath)) {
            return Optional.of(Files.readAllBytes(imagePath));
        }
        return Optional.empty();
    }
}