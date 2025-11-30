package com.multicore.crm.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private Long businessId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
}
