package com.multicore.crm.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerResponseDTO {
    private String message;
    private Long businessId;
    private Long ownerId;
    private String ownerEmail;
    private String businessName;
    private Boolean success;
}