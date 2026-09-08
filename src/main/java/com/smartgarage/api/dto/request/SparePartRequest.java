package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SparePartRequest {
    @NotNull(message = "Category id is required")
    private Long categoryId;

    private Long supplierId;

    @NotBlank(message = "Part name is required")
    private String partName;

    @NotBlank(message = "Part number is required")
    private String partNumber;

    @NotNull(message = "Unit price is required")
    @PositiveOrZero
    private BigDecimal unitPrice;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero
    private Integer stockQty;

    @PositiveOrZero
    private Integer reorderLevel = 5;
}
