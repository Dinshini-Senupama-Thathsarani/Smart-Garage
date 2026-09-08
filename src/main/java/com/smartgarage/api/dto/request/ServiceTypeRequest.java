package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceTypeRequest {
    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotBlank(message = "Service name is required")
    private String name;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private Integer estimatedMinutes;
}
