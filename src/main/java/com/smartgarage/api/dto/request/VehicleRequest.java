package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRequest {
    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Vehicle model id is required")
    private Long modelId;

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    private String chassisNo;
    private String color;
    private Integer mileage;
}
