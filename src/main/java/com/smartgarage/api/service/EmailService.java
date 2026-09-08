package com.smartgarage.api.service;

public interface EmailService {
    void sendLowStockAlert(String partName, String partNumber, int currentStock, int reorderLevel);
    void sendInvoiceNotification(String toEmail, String invoiceNumber, String totalAmount);
}
