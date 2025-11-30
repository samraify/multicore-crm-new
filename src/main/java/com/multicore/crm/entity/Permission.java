package com.multicore.crm.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 150)
    private PermissionName name;  // Enum

    @Column(length = 300)
    private String description;

    public enum PermissionName {
        VIEW_CUSTOMERS,
        CREATE_CUSTOMERS,
        EDIT_CUSTOMERS,
        DELETE_CUSTOMERS,
        VIEW_LEADS,
        CREATE_LEADS,
        EDIT_LEADS,
        DELETE_LEADS,
        VIEW_DEALS,
        CREATE_DEALS,
        EDIT_DEALS,
        DELETE_DEALS,
        VIEW_TICKETS,
        CREATE_TICKETS,
        EDIT_TICKETS,
        DELETE_TICKETS,
        MANAGE_USERS,
        VIEW_ANALYTICS,
        MANAGE_SUBSCRIPTIONS
    }
}
