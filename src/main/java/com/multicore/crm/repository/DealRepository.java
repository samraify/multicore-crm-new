package com.multicore.crm.repository;

import com.multicore.crm.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByBusinessId(Long businessId);
    List<Deal> findByBusinessIdAndCustomerId(Long businessId, Long customerId);
    List<Deal> findByBusinessIdAndStage(Long businessId, Deal.Stage stage);
}


