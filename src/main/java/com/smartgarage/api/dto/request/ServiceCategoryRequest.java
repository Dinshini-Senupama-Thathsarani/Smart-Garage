package com.smartgarage.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
