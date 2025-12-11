package com.multicore.crm.repository;

import com.multicore.crm.entity.CustomerBusinessMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerBusinessMatchRepository extends JpaRepository<CustomerBusinessMatch, Long> {

    List<CustomerBusinessMatch> findByCustomerId(Long customerId);

    Optional<CustomerBusinessMatch> findByCustomerIdAndBusinessId(Long customerId, Long businessId);
}

