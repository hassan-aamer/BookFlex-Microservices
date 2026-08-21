package com.bookflex.booking.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy Pattern — full refund policy.
 *
 * <p><b>Why this strategy here?</b>
 * Hotel rooms and flexible bookings typically allow full refunds regardless of timing.
 * This policy returns 100% of the booking amount on cancellation.</p>
 */
@Component
public class RefundablePolicy implements CancellationPolicy {

    @Override
    public BigDecimal calculateRefund(BigDecimal totalAmount, LocalDateTime bookingTime,
                                      LocalDateTime cancellationTime) {
        // Full refund regardless of when the cancellation happens
        return totalAmount;
    }

    @Override
    public String getPolicyName() {
        return "REFUNDABLE";
    }
}
