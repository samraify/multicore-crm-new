package com.multicore.crm.dto;

import com.multicore.crm.entity.Lead.LeadStatus;
import lombok.Data;

@Data
public class UpdateLeadDTO {
    private String name;
    private String email;
    private String phone;
    private String company;
    private String jobTitle;
    private LeadStatus status;
    private Integer score;
    private String notes;
}