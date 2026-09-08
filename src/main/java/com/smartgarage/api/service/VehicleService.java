package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.VehicleRequest;
import com.smartgarage.api.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    Vehicle create(VehicleRequest request);
    Vehicle getById(Long id);
    List<Vehicle> getAll();
    List<Vehicle> getByCustomerId(Long customerId);
    Vehicle update(Long id, VehicleRequest request);
    void delete(Long id);
}
