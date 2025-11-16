package com.example.gym.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "jdbc/GymDB",
    callerQuery = "SELECT password FROM users WHERE username = ?",
    groupsQuery = "SELECT role FROM users WHERE username = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class,
    priority = 10
)
@BasicAuthenticationMechanismDefinition(realmName = "gym-realm")
@ApplicationScoped
public class SecurityConfig {
}
