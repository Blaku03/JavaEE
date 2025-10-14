package com.example.lab0.configuration;

import com.example.lab0.user.controller.api.UserController;
import com.example.lab0.user.service.UserService;
import com.example.lab0.workoutsession.controller.api.WorkoutSessionController;
import com.example.lab0.workoutsession.service.WorkoutSessionService;
import com.example.lab0.workouttype.controller.api.WorkoutTypeController;
import com.example.lab0.workouttype.service.WorkoutTypeService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CreateControllers implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserService userService = (UserService) sce.getServletContext().getAttribute("userService");
        WorkoutTypeService workoutTypeService = (WorkoutTypeService) sce.getServletContext().getAttribute("workoutTypeService");
        WorkoutSessionService workoutSessionService = (WorkoutSessionService) sce.getServletContext().getAttribute("workoutSessionService");

        sce.getServletContext().setAttribute("userController", new UserController(userService, workoutSessionService));
        sce.getServletContext().setAttribute("workoutTypeController", new WorkoutTypeController(workoutTypeService, workoutSessionService));
        sce.getServletContext().setAttribute("workoutSessionController", new WorkoutSessionController(workoutSessionService, userService));
    }
}