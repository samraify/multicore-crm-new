package com.multicore.crm.dto.ticket;

import com.multicore.crm.entity.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTicketDTO {
    @NotNull
    private Long businessId;

    @NotBlank
    private String title;

    private String description;

    private Ticket.Priority priority = Ticket.Priority.MEDIUM;

    // optional assignment to a support agent
    private Long assignedToUserId;
}

