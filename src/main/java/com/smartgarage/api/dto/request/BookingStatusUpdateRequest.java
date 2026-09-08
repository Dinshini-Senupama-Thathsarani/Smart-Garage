package com.smartgarage.api.dto.request;

import com.smartgarage.api.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private BookingStatus status;
}
