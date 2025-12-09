package com.multicore.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deals")
@Data
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(nullable = false)
    private Long customerId; // or use @ManyToOne Customer

    private Long leadId; // or use @ManyToOne Lead

    @Column(nullable = false)
    private String title;

    @DecimalMin("0.0")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage = Stage.PROSPECT;

    @Min(0) @Max(100)
    private Integer probability;

    private LocalDate expectedCloseDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Stage { PROSPECT, NEGOTIATION, WON, LOST }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}