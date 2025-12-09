package com.multicore.crm.service;

import com.multicore.crm.dto.LoginRequest;
import com.multicore.crm.dto.LoginResponse;
import com.multicore.crm.dto.RegisterRequest;
import com.multicore.crm.dto.admin.CreateOwnerDTO;
import com.multicore.crm.entity.Business;
import com.multicore.crm.entity.Role;
import com.multicore.crm.entity.User;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.RoleRepository;
import com.multicore.crm.repository.UserRepository;
import com.multicore.crm.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       BusinessRepository businessRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ==================== ADMIN REGISTRATION ====================
    public LoginResponse registerAdmin(RegisterRequest request) {
        // Check if any admin already exists
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getRoleName() == Role.RoleType.SUPER_ADMIN));
        
        if (adminExists) {
            throw new RuntimeException("System admin already exists. Cannot register multiple admins.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role adminRole = roleRepository.findByRoleName(Role.RoleType.SUPER_ADMIN)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName(Role.RoleType.SUPER_ADMIN)
                            .description("System Administrator")
                            .build();
                    return roleRepository.save(newRole);
                });

        User admin = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .business(null)
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Arrays.asList(adminRole)))
                .build();

        User savedAdmin = userRepository.save(admin);
        log.info("Admin registered successfully: {}", savedAdmin.getEmail());

        String token = jwtUtil.generateToken(savedAdmin);
        return LoginResponse.builder()
                .token(token)
                .userId(savedAdmin.getId())
                .email(savedAdmin.getEmail())
                .fullName(savedAdmin.getFullName())
                .businessId(null)
                .role("SUPER_ADMIN")
                .message("Admin registered successfully")
                .success(true)
                .build();
    }

    // ==================== CUSTOMER REGISTRATION ====================
    public LoginResponse registerCustomer(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role customerRole = roleRepository.findByRoleName(Role.RoleType.CUSTOMER)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName(Role.RoleType.CUSTOMER)
                            .description("CRM Customer")
                            .build();
                    return roleRepository.save(newRole);
                });

        User customer = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .business(null)
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Arrays.asList(customerRole)))
                .build();

        User savedCustomer = userRepository.save(customer);
        log.info("Customer registered successfully: {}", savedCustomer.getEmail());

        String token = jwtUtil.generateToken(savedCustomer);
        return LoginResponse.builder()
                .token(token)
                .userId(savedCustomer.getId())
                .email(savedCustomer.getEmail())
                .fullName(savedCustomer.getFullName())
                .businessId(null)
                .role("CUSTOMER")
                .message("Customer registered successfully")
                .success(true)
                .build();
    }

    // ==================== LOGIN ====================
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("User account is inactive or suspended");
        }

        String primaryRole = user.getRoles().isEmpty() ? "CUSTOMER" : 
                user.getRoles().iterator().next().getRoleName().toString();

        String token = jwtUtil.generateToken(user);
        log.info("User logged in successfully: {} with role: {}", user.getEmail(), primaryRole);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .businessId(user.getBusiness() != null ? user.getBusiness().getId() : null)
                .role(primaryRole)
                .message("Login successful")
                .success(true)
                .build();
    }

    // ==================== CREATE BUSINESS ====================
    public Business createBusiness(String name, String description, String industry) {
        Business business = Business.builder()
                .name(name)
                .description(description)
                .industry(industry)
                .build();

        Business savedBusiness = businessRepository.save(business);
        log.info("Business created successfully: {}", savedBusiness.getName());
        return savedBusiness;
    }

    // ==================== CREATE OWNER ====================
    public LoginResponse createOwner(CreateOwnerDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        Role ownerRole = roleRepository.findByRoleName(Role.RoleType.BUSINESS_ADMIN)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName(Role.RoleType.BUSINESS_ADMIN)
                            .description("Business Owner")
                            .build();
                    return roleRepository.save(newRole);
                });

        User owner = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .business(business)
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Arrays.asList(ownerRole)))
                .build();

        User savedOwner = userRepository.save(owner);
        business.setOwner(savedOwner);
        businessRepository.save(business);
        log.info("Owner created successfully: {}", savedOwner.getEmail());

        String token = jwtUtil.generateToken(savedOwner);
        return LoginResponse.builder()
                .token(token)
                .userId(savedOwner.getId())
                .email(savedOwner.getEmail())
                .fullName(savedOwner.getFullName())
                .businessId(savedOwner.getBusiness().getId())
                .role("BUSINESS_ADMIN")
                .message("Owner created successfully")
                .success(true)
                .build();
    }

    // ==================== CREATE STAFF ====================
    public LoginResponse createStaff(Long businessId, String fullName, String email, String password, String phone, Role.RoleType roleType) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Only allow non-admin/internal staff roles
        if (roleType == null || roleType == Role.RoleType.SUPER_ADMIN || roleType == Role.RoleType.BUSINESS_ADMIN) {
            throw new RuntimeException("Invalid staff role");
        }

        Role staffRole = roleRepository.findByRoleName(roleType)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName(roleType)
                            .description("Staff Member")
                            .build();
                    return roleRepository.save(newRole);
                });

        User staff = User.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .business(business)
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Arrays.asList(staffRole)))
                .build();

        User savedStaff = userRepository.save(staff);
        log.info("Staff created successfully: {}", savedStaff.getEmail());

        String token = jwtUtil.generateToken(savedStaff);
        return LoginResponse.builder()
                .token(token)
                .userId(savedStaff.getId())
                .email(savedStaff.getEmail())
                .fullName(savedStaff.getFullName())
                .businessId(savedStaff.getBusiness().getId())
                .role(roleType.name())
                .message("Staff created successfully")
                .success(true)
                .build();
    }
}