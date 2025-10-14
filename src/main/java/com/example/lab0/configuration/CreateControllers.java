package com.example.lab0.configuration;

import com.example.lab0.user.controller.api.UserController;
import com.example.lab0.user.service.UserService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CreateControllers implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserService userService = (UserService) sce.getServletContext().getAttribute("userService");
        sce.getServletContext().setAttribute("userController", new UserController(userService));
    }
}