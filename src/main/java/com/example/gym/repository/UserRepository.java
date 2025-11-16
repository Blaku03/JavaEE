package com.example.gym.repository;

import com.example.gym.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository {

    @Inject
    private EntityManager em;

    public void save(User user) {
        em.merge(user);
    }

    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    public void delete(UUID id) {
        findById(id).ifPresent(user -> {
            em.remove(user);
        });
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }
}