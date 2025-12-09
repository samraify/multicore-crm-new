package com.multicore.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    @Column(nullable = true)
    private String description;

    public enum RoleType {
        SUPER_ADMIN,       // Platform-wide control
        BUSINESS_ADMIN,    // Business owner / admin
        SALES_MANAGER,     // Manages sales team and pipeline
        SALES_AGENT,       // Works assigned leads/tasks
        SUPPORT_MANAGER,   // Oversees support and SLAs
        SUPPORT_AGENT,     // Handles support tickets
        FINANCE,           // Billing and invoices
        VIEWER,            // Read-only access
        CUSTOMER           // External customer portal user
    }
}