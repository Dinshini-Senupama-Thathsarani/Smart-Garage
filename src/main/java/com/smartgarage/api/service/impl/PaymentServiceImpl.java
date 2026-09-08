package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.PaymentRequest;
import com.smartgarage.api.entity.Invoice;
import com.smartgarage.api.entity.Payment;
import com.smartgarage.api.enums.InvoiceStatus;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.InvoiceRepository;
import com.smartgarage.api.repository.PaymentRepository;
import com.smartgarage.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;


    @Override
    public Payment create(PaymentRequest request) {
        try {
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

            if (invoice.getStatus() == InvoiceStatus.PAID) {
                throw new IllegalStateException("Invoice " + invoice.getInvoiceNumber() + " is already fully paid");
            }

            Payment payment = new Payment();
            payment.setInvoice(invoice);
            payment.setPaymentDate(LocalDate.now());
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setTransactionRef(request.getTransactionRef());
            Payment saved = paymentRepository.save(payment);

            BigDecimal totalPaid = paymentRepository.findByInvoiceId(invoice.getId()).stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(InvoiceStatus.PAID);
            } else {
                invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
            }
            invoiceRepository.save(invoice);

            log.info("Payment of {} recorded for invoice {}", request.getAmount(), invoice.getInvoiceNumber());
            return saved;

        } catch (ResourceNotFoundException | IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error recording payment: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<Payment> getByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }
}
