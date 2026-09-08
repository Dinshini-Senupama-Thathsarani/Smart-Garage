package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.SupplierRequest;
import com.smartgarage.api.service.SupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, supplierService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, supplierService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, supplierService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, supplierService.update(id, request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, ResponseMessage.SUCCESS_MESSAGE));
    }
}
