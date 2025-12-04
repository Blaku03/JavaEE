package com.example.gym.model;

import com.example.gym.model.enums.WorkoutStatus;
import com.example.gym.validation.ValidTimeRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

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
@ValidTimeRange(message = "{validation.endTimeAfterStart}")  // Własny walidator klasy
public class WorkoutSession implements Serializable {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    // Optimistic locking - wersja do kontroli współbieżności
    @Version
    private Long version;

    // Automatyczne timestampy
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull(message = "{session.categoryRequired}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_type_id", nullable = false)
    private WorkoutType workoutType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "{session.startTimeRequired}")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull(message = "{validation.statusNotEmpty}")
    @Enumerated(EnumType.STRING)
    private WorkoutStatus status;

    // JPA lifecycle callbacks dla automatycznych timestampów
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}