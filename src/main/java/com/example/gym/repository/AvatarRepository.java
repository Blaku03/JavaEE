package com.example.gym.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class AvatarRepository {

    private final Path avatarDirectory;

    public AvatarRepository() {
        String userHome = System.getProperty("user.home");
        avatarDirectory = Paths.get(userHome, "gym_avatars");
        try {
            if (!Files.exists(avatarDirectory)) {
                Files.createDirectories(avatarDirectory);
            }
        } catch (IOException e) {
            // W środowisku produkcyjnym warto użyć loggera
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
        return null; // Zwracamy null, jeśli avatar nie istnieje
    }

    public void delete(UUID userId) throws IOException {
        Path avatarFile = avatarDirectory.resolve(userId.toString() + ".png");
        if (Files.exists(avatarFile)) {
            Files.delete(avatarFile);
        }
    }
}