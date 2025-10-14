package com.example.lab0.user.repository.api;

import com.example.lab0.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> find(UUID id);
    List<User> findAll();
    void create(User user);
}