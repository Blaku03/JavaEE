package com.example.gym.service;

import com.example.gym.repository.AvatarRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

public class AvatarService {

    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public void saveAvatar(UUID userId, InputStream inputStream) throws IOException {
        avatarRepository.save(userId, inputStream);
    }

    public Path getAvatar(UUID userId) {
        Path avatarPath = avatarRepository.find(userId);
        if (avatarPath == null) {
            return avatarRepository.getDefaultAvatar();
        }
        return avatarPath;
    }

    public void deleteAvatar(UUID userId) throws IOException {
        avatarRepository.delete(userId);
    }
}