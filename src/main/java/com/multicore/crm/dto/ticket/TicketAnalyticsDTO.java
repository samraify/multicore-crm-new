package com.multicore.crm.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TicketAnalyticsDTO {
    private long total;
    private long open;
    private long inProgress;
    private long resolved;
    private long closed;

    private long low;
    private long medium;
    private long high;
    private long urgent;
}

