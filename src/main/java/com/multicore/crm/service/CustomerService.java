package com.multicore.crm.service;

import com.multicore.crm.dto.CustomerBusinessMatchDto;
import com.multicore.crm.dto.CustomerDto;
import com.multicore.crm.entity.Business;
import com.multicore.crm.entity.Customer;
import com.multicore.crm.entity.CustomerBusinessMatch;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.CustomerBusinessMatchRepository;
import com.multicore.crm.repository.CustomerRepository;
import com.multicore.crm.repository.LeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;
    private final CustomerBusinessMatchRepository customerBusinessMatchRepository;
    private final LeadRepository leadRepository;

    public CustomerService(CustomerRepository customerRepository,
                           BusinessRepository businessRepository,
                           CustomerBusinessMatchRepository customerBusinessMatchRepository,
                           LeadRepository leadRepository) {
        this.customerRepository = customerRepository;
        this.businessRepository = businessRepository;
        this.customerBusinessMatchRepository = customerBusinessMatchRepository;
        this.leadRepository = leadRepository;
    }

    public Customer createCustomer(CustomerDto dto) {
        if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Customer email already exists");
        }
        Customer customer = dtoToEntity(dto);
        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {}", saved.getEmail());
        return saved;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer updateCustomer(Long id, CustomerDto dto) {
        Customer c = getCustomerById(id);
        if (dto.getName() != null) c.setName(dto.getName());
        if (dto.getPhone() != null) c.setPhone(dto.getPhone());
        // DTO does not include address/source in current project; only update fields present in DTO
        Customer updated = customerRepository.save(c);
        log.info("Customer updated: {}", updated.getId());
        return updated;
    }

    public void deleteCustomer(Long id) {
        Customer c = getCustomerById(id);
        customerRepository.delete(c);
        log.info("Customer deleted: {}", id);
    }

    public List<Customer> getCustomersByBusiness(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return business.getCustomerMatches().stream()
                .map(CustomerBusinessMatch::getCustomer)
                .collect(Collectors.toList());
    }

    /**
     * Returns businesses matched to a customer using explicit matches and historical leads.
     * Existing CRUD logic is untouched; this is additive only.
     */
    public List<CustomerBusinessMatchDto> getBusinessMatchesForCustomer(Long customerId) {
        // Validate existence; return matches scoped to this customer
        getCustomerById(customerId);

        Map<Long, CustomerBusinessMatchDto.CustomerBusinessMatchDtoBuilder> aggregated = new HashMap<>();

        // Explicit matches captured in customer_business_matches
        customerBusinessMatchRepository.findByCustomerId(customerId).forEach(match -> {
            Long businessId = match.getBusiness().getId();
            aggregated.put(businessId, CustomerBusinessMatchDto.builder()
                    .businessId(businessId)
                    .businessName(match.getBusiness().getName())
                    .industry(match.getBusiness().getIndustry())
                    .relationshipType(match.getRelationshipType())
                    .source("EXPLICIT_MATCH")
                    .lastInteractionAt(match.getUpdatedAt()));
        });

        // Historical leads linked to this customer (reflects prior interest/services)
        leadRepository.findByCustomerId(customerId).forEach(lead -> {
            Business business = lead.getBusiness();
            Long businessId = business.getId();
            LocalDateTime interactionTime = lead.getUpdatedAt();

            aggregated.compute(businessId, (id, builder) -> {
                if (builder == null) {
                    return CustomerBusinessMatchDto.builder()
                            .businessId(businessId)
                            .businessName(business.getName())
                            .industry(business.getIndustry())
                            .source("LEAD_HISTORY")
                            .lastInteractionAt(interactionTime);
                }
                // Enrich existing match with newer interaction timestamp if present
                if (interactionTime != null && builder.build().getLastInteractionAt() != null) {
                    if (interactionTime.isAfter(builder.build().getLastInteractionAt())) {
                        builder.lastInteractionAt(interactionTime);
                    }
                } else if (interactionTime != null) {
                    builder.lastInteractionAt(interactionTime);
                }
                return builder;
            });
        });

        return new ArrayList<>(aggregated.values()).stream()
                .map(CustomerBusinessMatchDto.CustomerBusinessMatchDtoBuilder::build)
                .collect(Collectors.toList());
    }

    private Customer dtoToEntity(CustomerDto dto) {
        return Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}
