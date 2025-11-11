package com.example.gym.model;

import jakarta.persistence.*; 
import lombok.*; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") 
@ToString(exclude = "workoutSessions") 
@Entity 
@Table(name = "workout_types") 
public class WorkoutType implements Serializable {

    @Id 
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String name;
    private String description;
    
    @OneToMany(
            mappedBy = "workoutType", 
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<WorkoutSession> workoutSessions = new ArrayList<>();
}