package com.bookflex.booking.policy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy Pattern — defines the contract for cancellation refund calculation.
 *
 * <p><b>Why Strategy Pattern here?</b>
 * Different resource types have different cancellation policies. A luxury hotel room
 * might offer full refunds, while a concert ticket might be non-refundable, and a
 * doctor's appointment might offer partial refunds if cancelled early enough.
 *
 * Without the Strategy Pattern, this logic would be an if/else chain in the cancellation
 * method, violating both OCP and SRP. With Strategy, each policy is a self-contained
 * class that can be swapped at runtime based on the resource type or booking terms.</p>
 *
 * <p><b>Interface Segregation</b>: This interface has exactly one responsibility —
 * calculate a refund amount. It doesn't concern itself with payment processing
 * (that's {@code PaymentGateway}) or notifications (that's {@code NotificationSender}).</p>
 */
public interface CancellationPolicy {

    /**
     * Calculates the refund amount based on the total booking amount and when the booking was made.
     *
     * @param totalAmount the original booking amount
     * @param bookingTime when the booking was created
     * @param cancellationTime when the cancellation was requested
     * @return the refund amount (between 0 and totalAmount inclusive)
     */
    BigDecimal calculateRefund(BigDecimal totalAmount, LocalDateTime bookingTime, LocalDateTime cancellationTime);

    /**
     * Returns the policy name for persistence and display.
     */
    String getPolicyName();
}
