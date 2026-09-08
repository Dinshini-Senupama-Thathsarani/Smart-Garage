package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.BookingRequest;
import com.smartgarage.api.entity.*;
import com.smartgarage.api.enums.BookingStatus;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.*;
import com.smartgarage.api.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final BookingServiceItemRepository bookingServiceItemRepository;

    @Override
    public Booking create(BookingRequest request) {
        try {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + request.getVehicleId()));

            Booking booking = new Booking();
            booking.setCustomer(customer);
            booking.setVehicle(vehicle);
            booking.setBookingDate(request.getBookingDate());
            booking.setNotes(request.getNotes());
            booking.setStatus(BookingStatus.PENDING);
            Booking savedBooking = bookingRepository.save(booking);

            // Attach the selected service types, snapshotting the price at time of booking
            for (Long serviceTypeId : request.getServiceTypeIds()) {
                ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Service type not found with id: " + serviceTypeId));

                BookingServiceItem item = new BookingServiceItem();
                item.setBooking(savedBooking);
                item.setServiceType(serviceType);
                item.setPriceAtBooking(serviceType.getBasePrice());
                bookingServiceItemRepository.save(item);
            }

            log.info("Booking created: id={} for customer={}", savedBooking.getId(), customer.getId());
            return getById(savedBooking.getId());

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating booking: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public Booking updateStatus(Long id, BookingStatus status) {
        Booking booking = getById(id);
        booking.setStatus(status);
        log.info("Booking {} status updated to {}", id, status);
        return bookingRepository.save(booking);
    }

    @Override
    public void delete(Long id) {
        Booking booking = getById(id);
        bookingRepository.delete(booking);
    }
}
