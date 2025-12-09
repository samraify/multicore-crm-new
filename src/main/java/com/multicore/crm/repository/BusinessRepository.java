package com.multicore.crm.repository;

import com.multicore.crm.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByName(String name);
    long countByActive(boolean active);
}