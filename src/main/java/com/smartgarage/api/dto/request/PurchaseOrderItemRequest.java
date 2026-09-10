package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderItemRequest {
    @NotNull(message = "Part id is required")
    private Long partId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Unit cost is required")
    @Positive(message = "Unit cost must be greater than zero")
    private BigDecimal unitCost;
}
