package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.PaymentRequest;
import com.smartgarage.api.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, paymentService.create(request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getByInvoice(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, paymentService.getByInvoiceId(invoiceId), ResponseMessage.SUCCESS_MESSAGE));
    }
}
