package com.example.gym.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

/**
 * Security configuration.
 * DatabaseIdentityStoreDefinition is used for REST API Basic Auth.
 * JSF FORM authentication uses Liberty BasicRegistry configured in server.xml.
 */
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "jdbc/GymDB",
    callerQuery = "SELECT password FROM users WHERE username = ?",
    groupsQuery = "SELECT role FROM users WHERE username = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class,
    priority = 10
)
@ApplicationScoped
public class SecurityConfig {
}
