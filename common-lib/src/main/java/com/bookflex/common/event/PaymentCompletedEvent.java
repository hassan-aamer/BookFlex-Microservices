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
 * Event published when payment is completed (success or failure).
 * Consumed by booking-service to advance the Saga (confirm or compensate).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent implements Serializable {

    private String paymentId;
    private String bookingId;
    private BigDecimal amount;
    private String status;          // SUCCESS or FAILED
    private String transactionReference;
    private String failureReason;
    private LocalDateTime completedAt;
}
