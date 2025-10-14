package com.example.lab0.user.repository.memory;

import com.example.lab0.User;
import com.example.lab0.datastore.DataStore;
import com.example.lab0.user.repository.api.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserInMemoryRepository implements UserRepository {

    private final DataStore store;

    public UserInMemoryRepository(DataStore store) {
        this.store = store;
    }

    @Override
    public Optional<User> find(UUID id) {
        return store.findUser(id);
    }

    @Override
    public List<User> findAll() {
        return store.findAllUsers();
    }

    @Override
    public void create(User user) {
        store.createUser(user);
    }
}