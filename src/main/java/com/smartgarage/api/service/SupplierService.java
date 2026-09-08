package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.SupplierRequest;
import com.smartgarage.api.entity.Supplier;

import java.util.List;

public interface SupplierService {
    Supplier create(SupplierRequest request);
    Supplier getById(Long id);
    List<Supplier> getAll();
    Supplier update(Long id, SupplierRequest request);
    void delete(Long id);
}
