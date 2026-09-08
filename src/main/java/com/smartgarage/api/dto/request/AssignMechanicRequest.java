package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignMechanicRequest {
    @NotNull(message = "Mechanic id is required")
    private Long mechanicId;

    private String roleInJob;
}
