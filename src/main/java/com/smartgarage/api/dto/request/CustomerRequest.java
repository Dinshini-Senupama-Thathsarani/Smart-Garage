package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String nic;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String address;
}
