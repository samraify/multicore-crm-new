package com.multicore.crm.service;

import com.multicore.crm.entity.Business;
import com.multicore.crm.entity.User;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class OwnerService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public OwnerService(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    /**
     * Get business details for owner
     */
    public Business getBusinessDetails(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    /**
     * Get owner's own details
     */
    public User getOwnerDetails(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}