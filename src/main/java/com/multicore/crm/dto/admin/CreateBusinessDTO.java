package com.multicore.crm.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBusinessDTO {
    @NotBlank(message = "Business name is required")
    private String name;

    private String description;

    private String industry;
}