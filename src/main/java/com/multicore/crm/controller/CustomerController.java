package com.multicore.crm.controller;

import com.multicore.crm.dto.CustomerDto;
import com.multicore.crm.dto.CustomerBusinessMatchDto;
import com.multicore.crm.entity.Customer;
import com.multicore.crm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody @Valid CustomerDto dto) {
        return customerService.createCustomer(dto);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerDto dto) {
        return customerService.updateCustomer(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @GetMapping("/{id}/business-matches")
    public List<CustomerBusinessMatchDto> getCustomerBusinessMatches(@PathVariable Long id) {
        return customerService.getBusinessMatchesForCustomer(id);
    }
}