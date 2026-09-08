package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.BookingRequest;
import com.smartgarage.api.dto.request.BookingStatusUpdateRequest;
import com.smartgarage.api.enums.BookingStatus;
import com.smartgarage.api.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, bookingService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, bookingService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll(@RequestParam(required = false) Long customerId,
                                                 @RequestParam(required = false) BookingStatus status) {
        if (customerId != null) {
            return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, bookingService.getByCustomerId(customerId), ResponseMessage.SUCCESS_MESSAGE));
        }
        if (status != null) {
            return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, bookingService.getByStatus(status), ResponseMessage.SUCCESS_MESSAGE));
        }
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, bookingService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody BookingStatusUpdateRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, bookingService.updateStatus(id, request.getStatus()), ResponseMessage.SUCCESS_MESSAGE));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, ResponseMessage.SUCCESS_MESSAGE));
    }
}
