package com.multicore.crm.controller;

import com.multicore.crm.dto.LoginResponse;
import com.multicore.crm.dto.admin.CreateBusinessDTO;
import com.multicore.crm.dto.admin.CreateOwnerDTO;
import com.multicore.crm.dto.admin.OwnerResponseDTO;
import com.multicore.crm.dto.admin.PlatformStatsDTO;
import com.multicore.crm.entity.Business;
import com.multicore.crm.service.AdminService;
import com.multicore.crm.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    public AdminController(AuthService authService, AdminService adminService) {
        this.authService = authService;
        this.adminService = adminService;
    }

    // ==================== CREATE BUSINESS ====================
    /**
     * POST /api/admin/create-business
     * Admin creates a new business (tenant)
     */
    @PostMapping("/create-business")
    public ResponseEntity<?> createBusiness(@Valid @RequestBody CreateBusinessDTO request) {
        try {
            Business business = authService.createBusiness(
                    request.getName(),
                    request.getDescription(),
                    request.getIndustry()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(OwnerResponseDTO.builder()
                            .message("Business created successfully")
                            .businessId(business.getId())
                            .businessName(business.getName())
                            .success(true)
                            .build());
        } catch (Exception e) {
            log.error("Business creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(OwnerResponseDTO.builder()
                            .message("Business creation failed: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // ==================== CREATE OWNER ====================
    /**
     * POST /api/admin/create-owner
     * Admin creates an owner for a business
     */
    @PostMapping("/create-owner")
    public ResponseEntity<LoginResponse> createOwner(@Valid @RequestBody CreateOwnerDTO request) {
        try {
            LoginResponse response = authService.createOwner(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Owner creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponse.builder()
                            .message("Owner creation failed: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // ==================== GET ALL BUSINESSES ====================
    /**
     * GET /api/admin/businesses
     * Admin gets all businesses in the system
     */
    @GetMapping("/businesses")
    public ResponseEntity<?> getAllBusinesses() {
        try {
            return ResponseEntity.ok(adminService.getAllBusinesses());
        } catch (Exception e) {
            log.error("Failed to fetch businesses: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch businesses: " + e.getMessage());
        }
    }

    // ==================== GET BUSINESS BY ID ====================
    /**
     * GET /api/admin/businesses/{businessId}
     * Admin gets a specific business
     */
    @GetMapping("/businesses/{businessId}")
    public ResponseEntity<?> getBusinessById(@PathVariable Long businessId) {
        try {
            return ResponseEntity.ok(adminService.getBusinessById(businessId));
        } catch (Exception e) {
            log.error("Failed to fetch business: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Business not found: " + e.getMessage());
        }
    }

    // ==================== ACTIVATE / DEACTIVATE BUSINESS ====================
    /**
     * PATCH /api/admin/businesses/{businessId}/status?active=true|false
     * Super Admin toggles business activation
     */
    @PatchMapping("/businesses/{businessId}/status")
    public ResponseEntity<?> toggleBusiness(@PathVariable Long businessId,
                                            @RequestParam boolean active) {
        try {
            Business business = adminService.setBusinessActive(businessId, active);
            return ResponseEntity.ok(OwnerResponseDTO.builder()
                    .message("Business status updated")
                    .businessId(business.getId())
                    .businessName(business.getName())
                    .success(true)
                    .build());
        } catch (Exception e) {
            log.error("Failed to update business status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(OwnerResponseDTO.builder()
                            .message("Failed to update business status: " + e.getMessage())
                            .success(false)
                            .build());
        }
    }

    // ==================== PLATFORM STATS ====================
    /**
     * GET /api/admin/stats
     * Super Admin overview counts
     */
    @GetMapping("/stats")
    public ResponseEntity<PlatformStatsDTO> getStats() {
        PlatformStatsDTO stats = adminService.getPlatformStats();
        return ResponseEntity.ok(stats);
    }
}