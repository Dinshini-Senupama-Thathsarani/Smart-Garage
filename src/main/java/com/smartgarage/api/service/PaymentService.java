package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.PaymentRequest;
import com.smartgarage.api.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment create(PaymentRequest request);
    List<Payment> getByInvoiceId(Long invoiceId);
}
