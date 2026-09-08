package com.smartgarage.api.repository;

import com.smartgarage.api.entity.BookingServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingServiceItemRepository extends JpaRepository<BookingServiceItem, Long> {
    List<BookingServiceItem> findByBookingId(Long bookingId);
}
