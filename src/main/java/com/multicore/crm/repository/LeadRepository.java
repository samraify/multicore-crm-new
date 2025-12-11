package com.multicore.crm.repository;

import com.multicore.crm.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByBusinessId(Long businessId);

    List<Lead> findByStatus(Lead.LeadStatus status);

    List<Lead> findByScoreGreaterThanEqual(Integer score);

    List<Lead> findByNameContainingIgnoreCase(String name);

    List<Lead> findByCustomerId(Long customerId);
}
