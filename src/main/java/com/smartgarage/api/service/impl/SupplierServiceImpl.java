package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.SupplierRequest;
import com.smartgarage.api.entity.Supplier;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.SupplierRepository;
import com.smartgarage.api.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Supplier create(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier getById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier update(Long id, SupplierRequest request) {
        Supplier supplier = getById(id);
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        return supplierRepository.save(supplier);
    }

    @Override
    public void delete(Long id) {
        supplierRepository.delete(getById(id));
    }
}
