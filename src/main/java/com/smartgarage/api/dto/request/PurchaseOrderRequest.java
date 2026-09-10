package com.smartgarage.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequest {
    @NotNull(message = "Supplier id is required")
    private Long supplierId;

    @NotEmpty(message = "Purchase order must include at least one item")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}
