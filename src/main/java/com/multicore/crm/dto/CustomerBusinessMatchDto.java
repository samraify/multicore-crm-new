package com.multicore.crm.dto;

import com.multicore.crm.entity.CustomerBusinessMatch;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerBusinessMatchDto {
    private Long businessId;
    private String businessName;
    private String industry;
    private CustomerBusinessMatch.RelationshipType relationshipType;
    private String source; // e.g., LEAD_HISTORY, EXPLICIT_MATCH
    private LocalDateTime lastInteractionAt;
}

