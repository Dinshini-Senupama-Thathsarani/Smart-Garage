package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.InvoiceCreateRequest;
import com.smartgarage.api.entity.Invoice;
import com.smartgarage.api.entity.JobCard;
import com.smartgarage.api.enums.InvoiceStatus;
import com.smartgarage.api.enums.JobCardStatus;
import com.smartgarage.api.exception.DuplicateResourceException;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.InvoiceRepository;
import com.smartgarage.api.repository.JobCardRepository;
import com.smartgarage.api.service.EmailService;
import com.smartgarage.api.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final JobCardRepository jobCardRepository;
    private final EmailService emailService;

    @Override
    public Invoice create(InvoiceCreateRequest request) {
        try {
            JobCard jobCard = jobCardRepository.findById(request.getJobCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job card not found with id: " + request.getJobCardId()));

            if (invoiceRepository.findByJobCardId(jobCard.getId()).isPresent()) {
                throw new DuplicateResourceException("An invoice already exists for job card id: " + jobCard.getId());
            }

            if (jobCard.getStatus() != JobCardStatus.COMPLETED) {
                throw new IllegalStateException("Job card must be COMPLETED before an invoice can be generated");
            }

            BigDecimal subtotal = jobCard.getTotalCost() == null ? BigDecimal.ZERO : jobCard.getTotalCost();
            BigDecimal tax = request.getTax() == null ? BigDecimal.ZERO : request.getTax();

            Invoice invoice = new Invoice();
            invoice.setJobCard(jobCard);
            invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            invoice.setIssueDate(LocalDate.now());
            invoice.setSubtotal(subtotal);
            invoice.setTax(tax);
            invoice.setTotalAmount(subtotal.add(tax));
            invoice.setStatus(InvoiceStatus.UNPAID);

            Invoice saved = invoiceRepository.save(invoice);
            log.info("Invoice {} generated for job card {}", saved.getInvoiceNumber(), jobCard.getId());

            // Bonus: notify the customer by email, if their account has one on file
            try {
                if (jobCard.getBooking() != null && jobCard.getBooking().getCustomer() != null
                        && jobCard.getBooking().getCustomer().getUser() != null) {
                    String customerEmail = jobCard.getBooking().getCustomer().getUser().getEmail();
                    emailService.sendInvoiceNotification(customerEmail, saved.getInvoiceNumber(), saved.getTotalAmount().toString());
                }
            } catch (Exception emailEx) {
                log.warn("Could not send invoice email notification: {}", emailEx.getMessage());
            }

            return saved;

        } catch (ResourceNotFoundException | DuplicateResourceException | IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating invoice: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public Invoice getById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }
}
