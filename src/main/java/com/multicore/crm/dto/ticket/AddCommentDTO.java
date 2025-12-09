package com.multicore.crm.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentDTO {
    @NotBlank
    private String comment;
}

