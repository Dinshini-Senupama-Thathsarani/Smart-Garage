package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.SparePartRequest;
import com.smartgarage.api.service.SparePartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/spare-parts")
@RequiredArgsConstructor
@Tag(name = "Spare Parts")
public class SparePartController {

    private final SparePartService sparePartService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody SparePartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/low-stock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getLowStock() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartService.getLowStock(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> update(@PathVariable Long id, @Valid @RequestBody SparePartRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartService.update(id, request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> delete(@PathVariable Long id) {
        sparePartService.delete(id);
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, ResponseMessage.SUCCESS_MESSAGE));
    }
}
