package com.multicore.crm.config;

import com.multicore.crm.entity.Role;
import com.multicore.crm.entity.Role.RoleType;
import com.multicore.crm.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByRoleName(roleType).orElseGet(() -> {
                Role role = new Role();
                role.setRoleName(roleType);
                role.setDescription(roleType.name().replace("_", " ").toLowerCase());
                return roleRepository.save(role);
            });
        }

        System.out.println("✔ Default roles ensured in database");
    }
}