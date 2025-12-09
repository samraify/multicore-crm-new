package com.multicore.crm.service;

import com.multicore.crm.dto.CreateLeadDTO;
import com.multicore.crm.dto.LeadDTO;
import com.multicore.crm.dto.UpdateLeadDTO;
import com.multicore.crm.entity.Business;
import com.multicore.crm.entity.Customer;
import com.multicore.crm.entity.Lead;
import com.multicore.crm.entity.User;
import com.multicore.crm.repository.BusinessRepository;
import com.multicore.crm.repository.CustomerRepository;
import com.multicore.crm.repository.LeadRepository;
import com.multicore.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public LeadServiceImpl(LeadRepository leadRepository, BusinessRepository businessRepository,
                           UserRepository userRepository, CustomerRepository customerRepository) {
        this.leadRepository = leadRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public LeadDTO createLead(CreateLeadDTO dto) {
        Business business = businessRepository.findById(dto.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        User assigned = null;
        if (dto.getAssignedToId() != null) {
            assigned = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (assigned.getBusiness() != null && !assigned.getBusiness().getId().equals(dto.getBusinessId())) {
                throw new RuntimeException("User does not belong to this business");
            }
        }

        Lead lead = Lead.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .company(dto.getCompany())
                .jobTitle(dto.getJobTitle())
                .business(business)
                .assignedTo(assigned)
                .status(Lead.LeadStatus.NEW)
                .score(0)
                .notes(dto.getNotes())
                .build();

        Lead saved = leadRepository.save(lead);
        log.info("Lead created: {}", saved.getId());
        return convertToDTO(saved);
    }

    @Override
    public LeadDTO getLead(Long id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));
        return convertToDTO(lead);
    }

    @Override
    public List<LeadDTO> getAllLeads(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));
        return business.getLeads().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public LeadDTO updateLead(Long id, UpdateLeadDTO dto) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));
        if (dto.getName() != null) lead.setName(dto.getName());
        if (dto.getEmail() != null) lead.setEmail(dto.getEmail());
        if (dto.getPhone() != null) lead.setPhone(dto.getPhone());
        if (dto.getCompany() != null) lead.setCompany(dto.getCompany());
        if (dto.getJobTitle() != null) lead.setJobTitle(dto.getJobTitle());
        if (dto.getStatus() != null) lead.setStatus(dto.getStatus());
        if (dto.getScore() != null) lead.setScore(dto.getScore());
        if (dto.getNotes() != null) lead.setNotes(dto.getNotes());
        Lead updated = leadRepository.save(lead);
        return convertToDTO(updated);
    }

    @Override
    public void deleteLead(Long id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));
        leadRepository.delete(lead);
        log.info("Lead deleted: {}", id);
    }

    @Override
    public LeadDTO updateStatus(Long id, String status) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setStatus(Lead.LeadStatus.valueOf(status));
        Lead updated = leadRepository.save(lead);
        return convertToDTO(updated);
    }

    @Override
    public LeadDTO updateScore(Long id, Integer score) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setScore(score);
        Lead updated = leadRepository.save(lead);
        return convertToDTO(updated);
    }

    @Override
    public LeadDTO convertToCustomer(Long id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));
        Customer customer = Customer.builder()
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .business(lead.getBusiness())
                .source("converted_from_lead")
                .build();
        Customer saved = customerRepository.save(customer);
        log.info("Lead {} converted to Customer {}", id, saved.getId());
        return convertToDTO(lead);
    }

    @Override
    public List<LeadDTO> searchByName(String name) {
        return leadRepository.findAll().stream()
                .filter(l -> l.getName() != null && l.getName().toLowerCase().contains(name.toLowerCase()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadDTO> filterLeads(Long businessId, Lead.LeadStatus status, Integer minScore, Integer maxScore) {
        return getAllLeads(businessId).stream()
                .filter(l -> status == null || l.getStatus() == status)
                .filter(l -> minScore == null || (l.getScore() != null && l.getScore() >= minScore))
                .filter(l -> maxScore == null || (l.getScore() != null && l.getScore() <= maxScore))
                .collect(Collectors.toList());
    }

    private LeadDTO convertToDTO(Lead lead) {
        return LeadDTO.builder()
                .id(lead.getId())
                .businessId(lead.getBusiness() != null ? lead.getBusiness().getId() : null)
                .customerId(lead.getCustomer() != null ? lead.getCustomer().getId() : null)
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .jobTitle(lead.getJobTitle())
                .status(lead.getStatus())
                .score(lead.getScore())
                .assignedToId(lead.getAssignedTo() != null ? lead.getAssignedTo().getId() : null)
                .notes(lead.getNotes())
                .build();
    }
}