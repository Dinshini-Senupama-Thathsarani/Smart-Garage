package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.VehicleRequest;
import com.smartgarage.api.entity.Vehicle;
import com.smartgarage.api.service.VehicleService;
import com.smartgarage.api.util.QrCodeUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final QrCodeUtil qrCodeUtil;

    @Value("${app.qrcode.base-url:http://localhost:8084/api/v1}")
    private String qrBaseUrl;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll(@RequestParam(required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleService.getByCustomerId(customerId), ResponseMessage.SUCCESS_MESSAGE));
        }
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, vehicleService.update(id, request), ResponseMessage.SUCCESS_MESSAGE));
    }


    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getById(id); // 404s via ResourceNotFoundException if missing
        String content = qrBaseUrl + "/vehicles/" + vehicle.getId();
        try {
            byte[] png = qrCodeUtil.generatePng(content, 300);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(png);
        } catch (Exception ex) {
            log.error("Failed to generate QR code for vehicle {}: {}", id, ex.getMessage());
            throw new RuntimeException("Could not generate QR code: " + ex.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, ResponseMessage.SUCCESS_MESSAGE));
    }
}
