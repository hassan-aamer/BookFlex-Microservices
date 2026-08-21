package com.bookflex.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a booking is initially created (status: PENDING).
 * Used to trigger resource reservation and initiate the payment flow.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent implements Serializable {

    private String bookingId;
    private String resourceId;
    private String resourceType;
    private String customerId;
    private String customerEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
