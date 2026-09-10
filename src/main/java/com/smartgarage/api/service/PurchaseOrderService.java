package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.PurchaseOrderRequest;
import com.smartgarage.api.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrder create(PurchaseOrderRequest request);
    PurchaseOrder getById(Long id);
    List<PurchaseOrder> getAll();
    PurchaseOrder receive(Long id);
    PurchaseOrder cancel(Long id);
}
