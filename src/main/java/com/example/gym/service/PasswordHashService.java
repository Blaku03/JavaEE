package com.example.gym.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

import java.util.Map;

@ApplicationScoped
public class PasswordHashService {

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @PostConstruct
    public void init() {
        passwordHash.initialize(Map.of(
            "Pbkdf2PasswordHash.Iterations", "3072",
            "Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256",
            "Pbkdf2PasswordHash.SaltSizeBytes", "64"
        ));
    }

    public String hashPassword(String plainPassword) {
        return passwordHash.generate(plainPassword.toCharArray());
    }
}
