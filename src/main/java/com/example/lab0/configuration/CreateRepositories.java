package com.example.lab0.configuration;

import com.example.lab0.datastore.DataStore;
import com.example.lab0.user.repository.memory.UserInMemoryRepository;
import com.example.lab0.workoutsession.repository.memory.WorkoutSessionInMemoryRepository;
import com.example.lab0.workouttype.repository.memory.WorkoutTypeInMemoryRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CreateRepositories implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataStore dataStore = (DataStore) sce.getServletContext().getAttribute("dataStore");
        sce.getServletContext().setAttribute("userRepository", new UserInMemoryRepository(dataStore));
        sce.getServletContext().setAttribute("workoutTypeRepository", new WorkoutTypeInMemoryRepository(dataStore));
        sce.getServletContext().setAttribute("workoutSessionRepository", new WorkoutSessionInMemoryRepository(dataStore));
    }
}