package com.example.lab0;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode(of = "id")
public class WorkoutSession implements Serializable {

    private UUID id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private WorkoutStatus status;

    @ToString.Exclude
    private User user;

    @ToString.Exclude
    private WorkoutType workoutType;

}