package com.bookflex.payment.gateway;

import com.bookflex.common.dto.PaymentRequest;
import com.bookflex.common.dto.PaymentResponse;

/**
 * Payment Gateway interface — abstracts the payment provider.
 *
 * <p><b>DIP (Dependency Inversion Principle)</b>:
 * The payment service depends on this interface (an abstraction), not on any
 * concrete payment provider (Stripe, PayPal, etc.). The concrete implementation
 * is injected via Spring DI.
 *
 * In production, you'd create {@code StripePaymentGateway}, {@code PayPalPaymentGateway},
 * etc. — each implementing this interface. Switching providers is a configuration change,
 * not a code change.</p>
 *
 * <p><b>ISP (Interface Segregation)</b>: This interface has exactly two methods —
 * process and refund. It doesn't handle subscription management, reporting, or
 * other payment concerns that would belong to separate interfaces.</p>
 */
public interface PaymentGateway {

    /**
     * Processes a payment for a booking.
     *
     * @param request payment details (amount, customer, booking reference)
     * @return payment result with status and transaction reference
     */
    PaymentResponse processPayment(PaymentRequest request);

    /**
     * Processes a refund for a cancelled booking.
     *
     * @param request refund details (original booking, amount to refund)
     * @return refund result
     */
    PaymentResponse processRefund(PaymentRequest request);
}
