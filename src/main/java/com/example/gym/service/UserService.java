package com.example.gym.service;

import com.example.gym.dto.CreateUserRequest;
import com.example.gym.dto.UpdateUserRequest;
import com.example.gym.model.User;
import com.example.gym.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AvatarService avatarService;

    @Inject
    private PasswordHashService passwordHashService;

    public UserService() {
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(CreateUserRequest request) {
        String hashedPassword = passwordHashService.hashPassword(
            request.getPassword() != null ? request.getPassword() : "defaultPassword123"
        );
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .username(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .role("user")
                .build();
        userRepository.save(newUser);
        return newUser;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> updateUser(UUID id, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(request.getName());
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }
        return userOptional;
    }

    public boolean deleteUser(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.delete(id);
            try {
                avatarService.deleteAvatar(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}