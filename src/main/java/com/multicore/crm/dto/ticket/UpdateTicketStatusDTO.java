package com.multicore.crm.dto.ticket;

import com.multicore.crm.entity.Ticket;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusDTO {
    @NotNull
    private Ticket.Status status;
}

