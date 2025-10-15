package com.example.gym.repository;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@ApplicationScoped
public class AvatarRepository {

    private final Path avatarDirectory = Paths.get(System.getProperty("user.home"), "gym_avatars");

    public AvatarRepository() {
        try {
            if (!Files.exists(avatarDirectory)) {
                Files.createDirectories(avatarDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(UUID userId, InputStream inputStream) throws IOException {
        Path avatarFile = avatarDirectory.resolve(userId.toString() + ".png");
        Files.copy(inputStream, avatarFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public Path find(UUID userId) {
        Path avatarFile = avatarDirectory.resolve(userId.toString() + ".png");
        if (Files.exists(avatarFile)) {
            return avatarFile;
        }
        return null;
    }

    public void delete(UUID userId) throws IOException {
        Path avatarFile = avatarDirectory.resolve(userId.toString() + ".png");
        if (Files.exists(avatarFile)) {
            Files.delete(avatarFile);
        }
    }

    public Path getDefaultAvatar() {
        return Paths.get(System.getProperty("user.home"), "gym_avatars", "default.png");
    }

    public boolean defaultAvatarExists() {
        return Files.exists(getDefaultAvatar());
    }

    public void saveDefaultAvatar(InputStream inputStream) throws IOException {
        Files.copy(inputStream, getDefaultAvatar(), StandardCopyOption.REPLACE_EXISTING);
    }
}