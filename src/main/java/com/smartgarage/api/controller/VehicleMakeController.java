package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.entity.VehicleMake;
import com.smartgarage.api.entity.VehicleModel;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.VehicleMakeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicle-makes")
@RequiredArgsConstructor
@Tag(name = "Vehicle Makes & Models")
public class VehicleMakeController {

    private final VehicleMakeRepository vehicleMakeRepository;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@RequestBody VehicleMake make) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleMakeRepository.save(make), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleMakeRepository.findAll(), ResponseMessage.SUCCESS_MESSAGE));
    }
}
