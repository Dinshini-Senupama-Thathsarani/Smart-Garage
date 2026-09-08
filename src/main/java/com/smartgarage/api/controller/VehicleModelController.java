package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.entity.VehicleMake;
import com.smartgarage.api.entity.VehicleModel;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.VehicleMakeRepository;
import com.smartgarage.api.repository.VehicleModelRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicle-models")
@RequiredArgsConstructor
@Tag(name = "Vehicle Makes & Models")
public class VehicleModelController {

    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleMakeRepository vehicleMakeRepository;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@RequestParam Long makeId, @RequestBody VehicleModel model) {
        VehicleMake make = vehicleMakeRepository.findById(makeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle make not found with id: " + makeId));
        model.setMake(make);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleModelRepository.save(model), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleModelRepository.findAll(), ResponseMessage.SUCCESS_MESSAGE));
    }
}
