package com.bookflex.resource.dto;

import com.bookflex.resource.domain.ResourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for creating a new bookable resource.
 * The {@code attributes} map holds type-specific fields (roomNumber, doctorName, etc.).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceRequest {

    @NotBlank(message = "Resource name is required")
    private String name;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    private String description;

    @NotNull(message = "Price per slot is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal pricePerSlot;

    /**
     * Type-specific attributes. Example for ROOM: {"roomNumber": "101", "capacity": "4", "floor": "2"}
     */
    private Map<String, String> attributes;
}
