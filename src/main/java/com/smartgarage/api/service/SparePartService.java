package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.SparePartRequest;
import com.smartgarage.api.entity.SparePart;

import java.util.List;

public interface SparePartService {
    SparePart create(SparePartRequest request);
    SparePart getById(Long id);
    List<SparePart> getAll();
    List<SparePart> getLowStock();
    SparePart update(Long id, SparePartRequest request);
    void delete(Long id);
}
