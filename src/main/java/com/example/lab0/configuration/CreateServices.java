package com.example.lab0.configuration;

import com.example.lab0.user.repository.api.UserRepository;
import com.example.lab0.user.service.UserService;
import com.example.lab0.workoutsession.repository.api.WorkoutSessionRepository;
import com.example.lab0.workoutsession.service.WorkoutSessionService;
import com.example.lab0.workouttype.repository.api.WorkoutTypeRepository;
import com.example.lab0.workouttype.service.WorkoutTypeService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class CreateServices implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserRepository userRepository = (UserRepository) sce.getServletContext().getAttribute("userRepository");
        WorkoutTypeRepository workoutTypeRepository = (WorkoutTypeRepository) sce.getServletContext().getAttribute("workoutTypeRepository");
        WorkoutSessionRepository workoutSessionRepository = (WorkoutSessionRepository) sce.getServletContext().getAttribute("workoutSessionRepository");

        sce.getServletContext().setAttribute("userService", new UserService(userRepository, sce.getServletContext()));
        sce.getServletContext().setAttribute("workoutTypeService", new WorkoutTypeService(workoutTypeRepository));
        sce.getServletContext().setAttribute("workoutSessionService", new WorkoutSessionService(workoutSessionRepository, userRepository, workoutTypeRepository, sce.getServletContext()));
    }
}