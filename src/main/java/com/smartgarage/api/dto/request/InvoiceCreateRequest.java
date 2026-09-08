package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceCreateRequest {
    @NotNull(message = "Job card id is required")
    private Long jobCardId;

    @PositiveOrZero(message = "Tax must be zero or positive")
    private BigDecimal tax = BigDecimal.ZERO;
}
