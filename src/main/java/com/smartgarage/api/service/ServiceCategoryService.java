package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.ServiceCategoryRequest;
import com.smartgarage.api.entity.ServiceCategory;

import java.util.List;

public interface ServiceCategoryService {
    ServiceCategory create(ServiceCategoryRequest request);
    ServiceCategory getById(Long id);
    List<ServiceCategory> getAll();
    ServiceCategory update(Long id, ServiceCategoryRequest request);
    void delete(Long id);
}
