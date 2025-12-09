package com.multicore.crm.service;

import com.multicore.crm.dto.CreateStaffDTO;
import com.multicore.crm.entity.Business;
import com.multicore.crm.entity.Role;
import com.multicore.crm.entity.User;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.RoleRepository;
import com.multicore.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;

@Slf4j
@Service
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, RoleRepository roleRepository,
                                  BusinessRepository businessRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a staff member in a business (Owner only)
     */
    public User createStaff(CreateStaffDTO request, Long businessId) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Get business
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Validate role
        Role.RoleType roleType = request.getRole();
        if (roleType == null || roleType == Role.RoleType.SUPER_ADMIN || roleType == Role.RoleType.BUSINESS_ADMIN) {
            throw new RuntimeException("Invalid staff role");
        }

        // Get or create role
        Role staffRole = roleRepository.findByRoleName(roleType)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName(roleType)
                            .description("Staff Member")
                            .build();
                    return roleRepository.save(newRole);
                });

        // Create staff user
        User staff = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .business(business)
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Arrays.asList(staffRole)))
                .build();

        User savedStaff = userRepository.save(staff);
        log.info("Staff created successfully: {}", savedStaff.getEmail());
        return savedStaff;
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}