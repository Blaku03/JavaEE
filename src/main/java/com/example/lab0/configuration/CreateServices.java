package com.example.lab0.configuration;

import com.example.lab0.user.repository.api.UserRepository;
import com.example.lab0.user.service.UserService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CreateServices implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserRepository userRepository = (UserRepository) sce.getServletContext().getAttribute("userRepository");
        sce.getServletContext().setAttribute("userService", new UserService(userRepository, sce.getServletContext()));
    }
}