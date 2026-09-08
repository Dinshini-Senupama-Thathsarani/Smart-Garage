package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.ServiceTypeRequest;
import com.smartgarage.api.entity.ServiceCategory;
import com.smartgarage.api.entity.ServiceType;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.ServiceCategoryRepository;
import com.smartgarage.api.repository.ServiceTypeRepository;
import com.smartgarage.api.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public ServiceType create(ServiceTypeRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + request.getCategoryId()));

        ServiceType serviceType = new ServiceType();
        serviceType.setCategory(category);
        serviceType.setName(request.getName());
        serviceType.setBasePrice(request.getBasePrice());
        serviceType.setEstimatedMinutes(request.getEstimatedMinutes());
        return serviceTypeRepository.save(serviceType);
    }

    @Override
    public ServiceType getById(Long id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found with id: " + id));
    }

    @Override
    public List<ServiceType> getAll() {
        return serviceTypeRepository.findAll();
    }

    @Override
    public ServiceType update(Long id, ServiceTypeRequest request) {
        ServiceType serviceType = getById(id);
        ServiceCategory category = serviceCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + request.getCategoryId()));

        serviceType.setCategory(category);
        serviceType.setName(request.getName());
        serviceType.setBasePrice(request.getBasePrice());
        serviceType.setEstimatedMinutes(request.getEstimatedMinutes());
        return serviceTypeRepository.save(serviceType);
    }

    @Override
    public void delete(Long id) {
        serviceTypeRepository.delete(getById(id));
    }
}
