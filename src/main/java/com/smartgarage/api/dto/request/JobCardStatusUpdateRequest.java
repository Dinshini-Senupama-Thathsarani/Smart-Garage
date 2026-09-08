package com.smartgarage.api.dto.request;

import com.smartgarage.api.enums.JobCardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobCardStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private JobCardStatus status;
}
