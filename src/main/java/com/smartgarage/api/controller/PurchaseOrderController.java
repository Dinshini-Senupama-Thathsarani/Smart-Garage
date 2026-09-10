package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.PurchaseOrderRequest;
import com.smartgarage.api.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, purchaseOrderService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, purchaseOrderService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, purchaseOrderService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}/receive", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> receive(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, purchaseOrderService.receive(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, purchaseOrderService.cancel(id), ResponseMessage.SUCCESS_MESSAGE));
    }
}
