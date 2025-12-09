package com.multicore.crm.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformStatsDTO {
    private long totalBusinesses;
    private long activeBusinesses;
    private long inactiveBusinesses;
    private long totalUsers;
    private long totalLeads;
    private long totalCustomers;
}

