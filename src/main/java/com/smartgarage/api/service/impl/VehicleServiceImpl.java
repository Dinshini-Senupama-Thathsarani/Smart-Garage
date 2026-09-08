package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.VehicleRequest;
import com.smartgarage.api.entity.Customer;
import com.smartgarage.api.entity.Vehicle;
import com.smartgarage.api.entity.VehicleModel;
import com.smartgarage.api.exception.DuplicateResourceException;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.CustomerRepository;
import com.smartgarage.api.repository.VehicleModelRepository;
import com.smartgarage.api.repository.VehicleRepository;
import com.smartgarage.api.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleModelRepository vehicleModelRepository;

    @Override
    public Vehicle create(VehicleRequest request) {
        try {
            if (vehicleRepository.findByPlateNumber(request.getPlateNumber()).isPresent()) {
                throw new DuplicateResourceException("Vehicle already registered with plate: " + request.getPlateNumber());
            }

            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

            VehicleModel model = vehicleModelRepository.findById(request.getModelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle model not found with id: " + request.getModelId()));

            Vehicle vehicle = new Vehicle();
            vehicle.setCustomer(customer);
            vehicle.setModel(model);
            vehicle.setPlateNumber(request.getPlateNumber());
            vehicle.setChassisNo(request.getChassisNo());
            vehicle.setColor(request.getColor());
            vehicle.setMileage(request.getMileage());

            return vehicleRepository.save(vehicle);
        } catch (DuplicateResourceException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating vehicle: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> getByCustomerId(Long customerId) {
        return vehicleRepository.findByCustomerId(customerId);
    }

    @Override
    public Vehicle update(Long id, VehicleRequest request) {
        Vehicle vehicle = getById(id);

        VehicleModel model = vehicleModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle model not found with id: " + request.getModelId()));

        vehicle.setModel(model);
        vehicle.setPlateNumber(request.getPlateNumber());
        vehicle.setChassisNo(request.getChassisNo());
        vehicle.setColor(request.getColor());
        vehicle.setMileage(request.getMileage());

        return vehicleRepository.save(vehicle);
    }

    @Override
    public void delete(Long id) {
        Vehicle vehicle = getById(id);
        vehicleRepository.delete(vehicle);
    }
}
