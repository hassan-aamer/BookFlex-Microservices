package com.bookflex.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lightweight resource representation shared between services.
 * Used by booking-service and payment-service to reference resources
 * without coupling to the full resource-service domain model.
 *
 * <p><b>DIP (Dependency Inversion)</b>: Services depend on this shared DTO (an abstraction)
 * rather than on the concrete ResourceEntity from resource-service.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {

    private String id;
    private String name;
    private String resourceType;
    private String description;
    private BigDecimal pricePerSlot;
    private String providerId;
    private String status;
    private LocalDateTime createdAt;
}
