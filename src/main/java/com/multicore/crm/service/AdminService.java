package com.multicore.crm.service;

import com.multicore.crm.dto.admin.PlatformStatsDTO;
import com.multicore.crm.entity.Business;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.CustomerRepository;
import com.multicore.crm.repository.LeadRepository;
import com.multicore.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AdminService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;

    public AdminService(BusinessRepository businessRepository,
                        UserRepository userRepository,
                        LeadRepository leadRepository,
                        CustomerRepository customerRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Get all businesses in the system
     */
    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    /**
     * Get a specific business by ID
     */
    public Business getBusinessById(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    /**
     * Activate or deactivate a business (tenant)
     */
    public Business setBusinessActive(Long businessId, boolean active) {
        Business business = getBusinessById(businessId);
        business.setActive(active);
        return businessRepository.save(business);
    }

    /**
     * Platform-level usage stats for Super Admin
     */
    public PlatformStatsDTO getPlatformStats() {
        long totalBusinesses = businessRepository.count();
        long activeBusinesses = businessRepository.countByActive(true);
        long inactiveBusinesses = businessRepository.countByActive(false);
        long totalUsers = userRepository.count();
        long totalLeads = leadRepository.count();
        long totalCustomers = customerRepository.count();

        return PlatformStatsDTO.builder()
                .totalBusinesses(totalBusinesses)
                .activeBusinesses(activeBusinesses)
                .inactiveBusinesses(inactiveBusinesses)
                .totalUsers(totalUsers)
                .totalLeads(totalLeads)
                .totalCustomers(totalCustomers)
                .build();
    }
}