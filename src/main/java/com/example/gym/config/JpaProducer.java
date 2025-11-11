package com.example.gym.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaProducer {

    @Produces
    @PersistenceContext(unitName = "gym-pu")
    private EntityManager em;
}