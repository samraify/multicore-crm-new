package com.multicore.crm.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    public enum RoleName { ADMIN, OWNER, STAFF, CUSTOMER }
}

