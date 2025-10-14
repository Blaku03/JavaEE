package com.example.lab0.datastore;

import com.example.lab0.User;

import java.util.*;
import java.util.stream.Collectors;

public class DataStore {

    private final Set<User> users = new HashSet<>();

    public synchronized List<User> findAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized void createUser(User user) {
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new IllegalArgumentException("User with id " + user.getId() + " already exists");
        }
        users.add(user);
    }

    public synchronized Optional<User> findUser(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
}