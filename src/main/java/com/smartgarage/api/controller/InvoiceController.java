package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.InvoiceCreateRequest;
import com.smartgarage.api.service.InvoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody InvoiceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, invoiceService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, invoiceService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, invoiceService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }
}
