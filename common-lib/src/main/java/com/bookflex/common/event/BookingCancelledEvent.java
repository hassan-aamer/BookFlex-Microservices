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
 * Event published when a booking is cancelled.
 * Consumed by notification-service (to notify the customer),
 * resource-service (to release the reserved slot), and
 * payment-service (to process a refund if applicable).
 *
 * <p><b>Observer Pattern + Saga compensating transaction</b>:
 * This event triggers compensating actions across multiple services
 * as part of the Saga pattern's rollback mechanism.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent implements Serializable {

    private String bookingId;
    private String resourceId;
    private String resourceName;
    private String customerId;
    private String customerEmail;
    private String customerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal refundAmount;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
}
