package com.example.gym.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private String email;
    private List<WorkoutSession> workoutSessions;
}