package com.example.gym.repository;

import com.example.gym.datastore.DataStore;
import com.example.gym.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private final DataStore dataStore;

    public UserRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<User> findAll() {
        return dataStore.getUsers();
    }

    public Optional<User> findById(UUID id) {
        return dataStore.getUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public void save(User user) {
        dataStore.getUsers().add(user);
    }

    public void delete(User user) {
        dataStore.getUsers().remove(user);
    }
}