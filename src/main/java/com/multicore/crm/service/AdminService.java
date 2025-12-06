package com.multicore.crm.service;

import com.multicore.crm.entity.Business;
import com.multicore.crm.repository.BusinessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AdminService {

    private final BusinessRepository businessRepository;

    public AdminService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
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
}