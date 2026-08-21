package com.bookflex.booking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a new booking.
 *
 * <p>The booking-service doesn't know the concrete resource type — it only
 * receives a resourceId and checks availability via the resource-service.
 * This is the practical application of the Open/Closed Principle.</p>
 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateBookingRequest {

    @NotBlank(message = "Resource ID is required")
    private String resourceId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private String notes;

    private String cancellationPolicy; // REFUNDABLE, NON_REFUNDABLE, PARTIAL_REFUND
}
