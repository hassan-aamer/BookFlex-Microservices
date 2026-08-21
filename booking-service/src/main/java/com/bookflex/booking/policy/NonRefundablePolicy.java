package com.bookflex.booking.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy Pattern — no refund policy.
 *
 * <p><b>Why this strategy here?</b>
 * Some bookings (e.g., discounted rates, event tickets) are non-refundable.
 * This policy always returns zero regardless of timing.</p>
 */
@Component
public class NonRefundablePolicy implements CancellationPolicy {

    @Override
    public BigDecimal calculateRefund(BigDecimal totalAmount, LocalDateTime bookingTime,
                                      LocalDateTime cancellationTime) {
        return BigDecimal.ZERO;
    }

    @Override
    public String getPolicyName() {
        return "NON_REFUNDABLE";
    }
}
