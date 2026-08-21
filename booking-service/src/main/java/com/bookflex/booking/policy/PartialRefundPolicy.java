package com.bookflex.booking.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Strategy Pattern — partial refund policy with time-based degradation.
 *
 * <p><b>Why this strategy here?</b>
 * Medical appointments and sports field bookings typically allow partial refunds
 * if cancelled well in advance, but no refund for last-minute cancellations.
 * This encourages early cancellation so the resource can be re-booked.</p>
 *
 * <p><b>Policy rules:</b>
 * <ul>
 *   <li>Cancelled more than 24 hours before start → 50% refund</li>
 *   <li>Cancelled within 24 hours of start → 0% refund</li>
 * </ul>
 * </p>
 */
@Component
public class PartialRefundPolicy implements CancellationPolicy {

    private static final long FULL_REFUND_HOURS_THRESHOLD = 24;
    private static final BigDecimal PARTIAL_REFUND_PERCENTAGE = new BigDecimal("0.50");

    @Override
    public BigDecimal calculateRefund(BigDecimal totalAmount, LocalDateTime bookingTime,
                                      LocalDateTime cancellationTime) {
        Duration timeUntilBooking = Duration.between(cancellationTime, bookingTime);
        long hoursUntilBooking = timeUntilBooking.toHours();

        if (hoursUntilBooking >= FULL_REFUND_HOURS_THRESHOLD) {
            // 50% refund if cancelled > 24 hours before the booking
            return totalAmount.multiply(PARTIAL_REFUND_PERCENTAGE)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // No refund for last-minute cancellations
        return BigDecimal.ZERO;
    }

    @Override
    public String getPolicyName() {
        return "PARTIAL_REFUND";
    }
}
