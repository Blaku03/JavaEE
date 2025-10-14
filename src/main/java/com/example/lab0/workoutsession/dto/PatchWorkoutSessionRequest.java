package com.example.lab0.workoutsession.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PatchWorkoutSessionRequest {
    private String name;
}