package com.smartgarage.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EmailServiceImpl implements com.smartgarage.api.service.EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@smartgarage.lk}")
    private String fromAddress;

    @Value("${app.mail.admin-email:admin@smartgarage.lk}")
    private String adminEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendLowStockAlert(String partName, String partNumber, int currentStock, int reorderLevel) {
        String subject = "[Smart Garage] Low Stock Alert: " + partName;
        String body = String.format(
                "Part '%s' (Part No: %s) is running low on stock.%n" +
                        "Current stock: %d%nReorder level: %d%n%nPlease place a purchase order soon.",
                partName, partNumber, currentStock, reorderLevel);
        send(adminEmail, subject, body, "low-stock alert for " + partName);
    }

    @Override
    public void sendInvoiceNotification(String toEmail, String invoiceNumber, String totalAmount) {
        String subject = "[Smart Garage] Invoice " + invoiceNumber + " Generated";
        String body = String.format(
                "Your invoice %s has been generated.%nTotal amount due: Rs. %s%n%nThank you for choosing Smart Garage!",
                invoiceNumber, totalAmount);
        send(toEmail, subject, body, "invoice notification " + invoiceNumber);
    }

    private void send(String to, String subject, String body, String description) {
        if (!mailEnabled || to == null || to.isBlank()) {
            log.info("[Email skipped - app.mail.enabled=false] Would send '{}' to {}", description, to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent: {} to {}", description, to);
        } catch (Exception ex) {
            // Never let a failed email crash the actual business operation (e.g. stock deduction, invoice creation)
            log.error("Failed to send email ({}): {}", description, ex.getMessage());
        }
    }
}
