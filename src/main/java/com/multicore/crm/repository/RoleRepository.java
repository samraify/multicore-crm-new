package com.multicore.crm.repository;

import com.multicore.crm.entity.Role;
import com.multicore.crm.entity.Role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleType roleName);
}