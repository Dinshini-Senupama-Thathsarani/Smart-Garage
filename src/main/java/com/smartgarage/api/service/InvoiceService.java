package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.InvoiceCreateRequest;
import com.smartgarage.api.entity.Invoice;

import java.util.List;

public interface InvoiceService {
    Invoice create(InvoiceCreateRequest request);
    Invoice getById(Long id);
    List<Invoice> getAll();
}
