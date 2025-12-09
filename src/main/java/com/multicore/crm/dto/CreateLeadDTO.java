package com.multicore.crm.dto;

import lombok.Data;

@Data
public class CreateLeadDTO {
    private Long businessId;
    private Long customerId;
    private Long assignedToId;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String jobTitle;
    private String notes;
}