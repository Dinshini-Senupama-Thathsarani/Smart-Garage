package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.BookingRequest;
import com.smartgarage.api.entity.Booking;
import com.smartgarage.api.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking create(BookingRequest request);
    Booking getById(Long id);
    List<Booking> getAll();
    List<Booking> getByCustomerId(Long customerId);
    List<Booking> getByStatus(BookingStatus status);
    Booking updateStatus(Long id, BookingStatus status);
    void delete(Long id);
}
