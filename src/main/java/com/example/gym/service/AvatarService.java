package com.example.gym.service;

import com.example.gym.repository.AvatarRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@ApplicationScoped
public class AvatarService {

    @Inject 
    private AvatarRepository avatarRepository;

    public AvatarService() {
    }

    public void saveAvatar(UUID userId, InputStream inputStream) throws IOException {
        avatarRepository.save(userId, inputStream);
    }

    public Path getAvatar(UUID userId) {
        return avatarRepository.find(userId);
    }

    public void deleteAvatar(UUID userId) throws IOException {
        avatarRepository.delete(userId);
    }
}