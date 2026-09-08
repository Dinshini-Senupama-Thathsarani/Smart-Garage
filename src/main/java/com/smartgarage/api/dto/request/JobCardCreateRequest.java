package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobCardCreateRequest {
    @NotNull(message = "Booking id is required")
    private Long bookingId;
}
