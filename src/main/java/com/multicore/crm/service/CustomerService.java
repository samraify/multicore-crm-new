package com.multicore.crm.service;

import com.multicore.crm.dto.CustomerDto;
import com.multicore.crm.entity.Business;
import com.multicore.crm.entity.Customer;
import com.multicore.crm.entity.CustomerBusinessMatch;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;

    public CustomerService(CustomerRepository customerRepository, BusinessRepository businessRepository) {
        this.customerRepository = customerRepository;
        this.businessRepository = businessRepository;
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

    private Customer dtoToEntity(CustomerDto dto) {
        return Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}
