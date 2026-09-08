package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.SparePartRequest;
import com.smartgarage.api.entity.SparePart;
import com.smartgarage.api.entity.SparePartCategory;
import com.smartgarage.api.entity.Supplier;
import com.smartgarage.api.exception.DuplicateResourceException;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.SparePartCategoryRepository;
import com.smartgarage.api.repository.SparePartRepository;
import com.smartgarage.api.repository.SupplierRepository;
import com.smartgarage.api.service.SparePartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SparePartServiceImpl implements SparePartService {

    private final SparePartRepository sparePartRepository;
    private final SparePartCategoryRepository sparePartCategoryRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public SparePart create(SparePartRequest request) {
        if (sparePartRepository.findByPartNumber(request.getPartNumber()).isPresent()) {
            throw new DuplicateResourceException("Part number already exists: " + request.getPartNumber());
        }

        SparePartCategory category = sparePartCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Spare part category not found with id: " + request.getCategoryId()));

        SparePart part = new SparePart();
        part.setCategory(category);

        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));
            part.setSupplier(supplier);
        }

        part.setPartName(request.getPartName());
        part.setPartNumber(request.getPartNumber());
        part.setUnitPrice(request.getUnitPrice());
        part.setStockQty(request.getStockQty());
        part.setReorderLevel(request.getReorderLevel());

        return sparePartRepository.save(part);
    }

    @Override
    public SparePart getById(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with id: " + id));
    }

    @Override
    public List<SparePart> getAll() {
        return sparePartRepository.findAll();
    }

    @Override
    public List<SparePart> getLowStock() {
        return sparePartRepository.findLowStockParts();
    }

    @Override
    public SparePart update(Long id, SparePartRequest request) {
        SparePart part = getById(id);

        SparePartCategory category = sparePartCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Spare part category not found with id: " + request.getCategoryId()));
        part.setCategory(category);

        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));
            part.setSupplier(supplier);
        }

        part.setPartName(request.getPartName());
        part.setUnitPrice(request.getUnitPrice());
        part.setStockQty(request.getStockQty());
        part.setReorderLevel(request.getReorderLevel());

        return sparePartRepository.save(part);
    }

    @Override
    public void delete(Long id) {
        sparePartRepository.delete(getById(id));
    }
}
