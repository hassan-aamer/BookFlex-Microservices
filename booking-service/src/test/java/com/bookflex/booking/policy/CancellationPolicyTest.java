package com.bookflex.booking.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CancellationPolicyTest {

    private final RefundablePolicy refundablePolicy = new RefundablePolicy();
    private final NonRefundablePolicy nonRefundablePolicy = new NonRefundablePolicy();
    private final PartialRefundPolicy partialRefundPolicy = new PartialRefundPolicy();

    @Test
    @DisplayName("Strategy Pattern: RefundablePolicy returns 100% refund")
    void testRefundablePolicy() {
        BigDecimal total = new BigDecimal("200.00");
        LocalDateTime now = LocalDateTime.now();

        BigDecimal refund = refundablePolicy.calculateRefund(total, now.plusDays(2), now);
        assertEquals(new BigDecimal("200.00"), refund);
    }

    @Test
    @DisplayName("Strategy Pattern: NonRefundablePolicy returns 0 refund")
    void testNonRefundablePolicy() {
        BigDecimal total = new BigDecimal("200.00");
        LocalDateTime now = LocalDateTime.now();

        BigDecimal refund = nonRefundablePolicy.calculateRefund(total, now.plusDays(5), now);
        assertEquals(BigDecimal.ZERO, refund);
    }

    @Test
    @DisplayName("Strategy Pattern: PartialRefundPolicy returns 50% if >24h, 0 if <24h")
    void testPartialRefundPolicy() {
        BigDecimal total = new BigDecimal("200.00");
        LocalDateTime now = LocalDateTime.now();

        // > 24 hours before booking start -> 50% refund
        BigDecimal earlyRefund = partialRefundPolicy.calculateRefund(total, now.plusHours(48), now);
        assertEquals(new BigDecimal("100.00"), earlyRefund);

        // < 24 hours before booking start -> 0 refund
        BigDecimal lateRefund = partialRefundPolicy.calculateRefund(total, now.plusHours(12), now);
        assertEquals(BigDecimal.ZERO, lateRefund);
    }
}
