package com.example.gym.model;

import com.example.gym.model.enums.WorkoutStatus;
import jakarta.persistence.*; // Importy JPA
import lombok.*; // Importy Lomboka

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"workoutType", "user"})
@Entity
@Table(name = "workout_sessions")
public class WorkoutSession implements Serializable {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_type_id", nullable = false)
    private WorkoutType workoutType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private WorkoutStatus status;
}