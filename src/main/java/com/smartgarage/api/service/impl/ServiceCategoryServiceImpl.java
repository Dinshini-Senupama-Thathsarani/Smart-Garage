package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.ServiceCategoryRequest;
import com.smartgarage.api.entity.ServiceCategory;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.ServiceCategoryRepository;
import com.smartgarage.api.service.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public ServiceCategory create(ServiceCategoryRequest request) {
        ServiceCategory category = new ServiceCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return serviceCategoryRepository.save(category);
    }

    @Override
    public ServiceCategory getById(Long id) {
        return serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + id));
    }

    @Override
    public List<ServiceCategory> getAll() {
        return serviceCategoryRepository.findAll();
    }

    @Override
    public ServiceCategory update(Long id, ServiceCategoryRequest request) {
        ServiceCategory category = getById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return serviceCategoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        serviceCategoryRepository.delete(getById(id));
    }
}
