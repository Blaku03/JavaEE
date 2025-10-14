package com.example.lab0.configuration;

import com.example.lab0.datastore.DataStore;
import com.example.lab0.user.repository.memory.UserInMemoryRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CreateRepositories implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataStore dataStore = (DataStore) sce.getServletContext().getAttribute("dataStore");
        sce.getServletContext().setAttribute("userRepository", new UserInMemoryRepository(dataStore));
    }
}