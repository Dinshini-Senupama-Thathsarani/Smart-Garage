package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.ServiceTypeRequest;
import com.smartgarage.api.entity.ServiceType;

import java.util.List;

public interface ServiceTypeService {
    ServiceType create(ServiceTypeRequest request);
    ServiceType getById(Long id);
    List<ServiceType> getAll();
    ServiceType update(Long id, ServiceTypeRequest request);
    void delete(Long id);
}
