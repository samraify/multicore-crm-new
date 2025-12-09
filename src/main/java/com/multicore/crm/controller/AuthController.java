package com.multicore.crm.controller;

import com.multicore.crm.dto.LoginRequest;
import com.multicore.crm.dto.LoginResponse;
import com.multicore.crm.dto.RegisterRequest;
import com.multicore.crm.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ==================== CUSTOMER REGISTRATION ====================
    /**
     * POST /api/auth/register/customer
     * Public signup for CRM customers
     */
    @PostMapping("/register/customer")
    public ResponseEntity<LoginResponse> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.registerCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Customer registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponse.builder()
                            .message("Registration failed: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // ==================== LOGIN ====================
    /**
     * POST /api/auth/login
     * Single login endpoint for ADMIN, OWNER, STAFF, CUSTOMER
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .message("Login failed: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }
}