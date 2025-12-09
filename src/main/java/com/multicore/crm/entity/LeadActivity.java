package com.multicore.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    // Score increment for this activity (increases lead score)
    @Column(nullable = false)
    private Integer scorePoints = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ActivityType {
        CALL, EMAIL, MEETING, NOTE, TASK_COMPLETED, ENGAGEMENT, OTHER
    }
}