package com.smartgarage.api.repository;

import com.smartgarage.api.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByJobCardId(Long jobCardId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
