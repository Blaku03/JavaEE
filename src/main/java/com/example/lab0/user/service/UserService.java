package com.example.lab0.user.service;

import com.example.lab0.User;
import com.example.lab0.user.repository.api.UserRepository;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository repository;
    private final Path avatarDirectory;

    public UserService(UserRepository repository, ServletContext context) {
        this.repository = repository;
        String avatarDir = context.getInitParameter("avatar.directory");
        this.avatarDirectory = Paths.get(avatarDir);
        if (!Files.exists(avatarDirectory)) {
            try {
                Files.createDirectories(avatarDirectory);
            } catch (IOException e) {
                throw new IllegalStateException("Could not create avatar directory", e);
            }
        }
    }

    public Optional<User> find(UUID id) {
        return repository.find(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void create(User user) {
        repository.create(user);
    }

    public void storeAvatar(UUID userId, InputStream inputStream) throws IOException {
        Path avatarPath = avatarDirectory.resolve(userId.toString());
        Files.copy(inputStream, avatarPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public Optional<byte[]> getAvatar(UUID userId) throws IOException {
        Path avatarPath = avatarDirectory.resolve(userId.toString());
        if (Files.exists(avatarPath)) {
            return Optional.of(Files.readAllBytes(avatarPath));
        }
        return Optional.empty();
    }

    public void deleteAvatar(UUID userId) throws IOException {
        Path avatarPath = avatarDirectory.resolve(userId.toString());
        if (Files.exists(avatarPath)) {
            Files.delete(avatarPath);
        }
    }
}