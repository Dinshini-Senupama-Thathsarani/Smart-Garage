package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Vehicle id is required")
    private Long vehicleId;

    @NotNull(message = "Booking date is required")
    private LocalDateTime bookingDate;

    @NotEmpty(message = "At least one service type must be selected")
    private List<Long> serviceTypeIds;

    private String notes;
}
