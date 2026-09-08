package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {
    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;
    private String phone;
    private String email;
    private String address;
}
