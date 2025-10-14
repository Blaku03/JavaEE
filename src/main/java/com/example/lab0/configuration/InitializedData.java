package com.example.lab0.configuration;

import com.example.lab0.User;
import com.example.lab0.user.service.UserService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDate;
import java.util.UUID;

@WebListener
public class InitializedData implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserService userService = (UserService) sce.getServletContext().getAttribute("userService");
        init(userService);
    }

    private void init(UserService userService) {
        User user1 = User.builder()
                .id(UUID.fromString("c4804e0f-769e-4ab9-9ebe-0578fb4f00a1"))
                .username("john_doe")
                .email("john.doe@example.com")
                .registrationDate(LocalDate.of(2023, 1, 15))
                .build();
        User user2 = User.builder()
                .id(UUID.fromString("81e1c2a9-7f57-439b-b53d-6db88b071e4e"))
                .username("jane_smith")
                .email("jane.smith@example.com")
                .registrationDate(LocalDate.of(2023, 3, 22))
                .build();
        User user3 = User.builder()
                .id(UUID.fromString("ed6cfb2a-cad7-47dd-9b56-9d1e3c7a4197"))
                .username("alice_wonder")
                .email("alice.wonder@example.com")
                .registrationDate(LocalDate.of(2022, 11, 30))
                .build();
        User user4 = User.builder()
                .id(UUID.fromString("525d3e7b-bb1f-4c13-bf17-926d1a12e4c0"))
                .username("bob_builder")
                .email("bob.builder@example.com")
                .registrationDate(LocalDate.of(2023, 5, 1))
                .build();

        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.create(user4);
    }
}