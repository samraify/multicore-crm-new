package com.multicore.crm.dto;

import com.multicore.crm.entity.Lead.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadDTO {
    private Long id;
    private Long businessId;
    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String jobTitle;
    private LeadStatus status;
    private Integer score;
    private Long assignedToId;
    private String notes;
}