package com.example.gym.service;

import com.example.gym.dto.CreateUserRequest;
import com.example.gym.dto.UpdateUserRequest;
import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;
    private final AvatarService avatarService;

    public UserService(UserRepository userRepository, AvatarService avatarService) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User createUser(CreateUserRequest request) {
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .workoutSessions(new ArrayList<>())
                .build();
        userRepository.save(newUser);
        return newUser;
    }

    public Optional<User> updateUser(UUID id, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }
        return userOptional;
    }

    public boolean deleteUser(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            try {
                // Also delete avatar
                avatarService.deleteAvatar(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}