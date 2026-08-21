package com.bookflex.payment.gateway;

import com.bookflex.common.dto.PaymentRequest;
import com.bookflex.common.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

/**
 * Mock Payment Gateway — simulates real payment processing.
 *
 * <p><b>DIP in action</b>: This is the concrete implementation of {@link PaymentGateway}.
 * In production, this would be replaced with a Stripe/PayPal integration implementing
 * the same interface. The service layer never changes — only the injected bean does.</p>
 *
 * <p>The mock simulates:
 * <ul>
 *   <li>90% success rate (to test the happy path)</li>
 *   <li>10% failure rate (to test Saga compensating transactions)</li>
 *   <li>100ms artificial delay (to simulate network latency)</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class MockPaymentGateway implements PaymentGateway {

    private static final double SUCCESS_RATE = 0.90;
    private final Random random = new Random();

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("[MockPaymentGateway] Processing payment of {} for booking {}",
                request.getAmount(), request.getBookingId());

        // Simulate processing delay
        simulateDelay();

        boolean isSuccess = random.nextDouble() < SUCCESS_RATE;

        if (isSuccess) {
            String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            log.info("[MockPaymentGateway] Payment SUCCESS — txn: {}", transactionRef);

            return PaymentResponse.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .bookingId(request.getBookingId())
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .transactionReference(transactionRef)
                    .message("Payment processed successfully")
                    .build();
        } else {
            log.warn("[MockPaymentGateway] Payment FAILED for booking {}", request.getBookingId());

            return PaymentResponse.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .bookingId(request.getBookingId())
                    .amount(request.getAmount())
                    .status("FAILED")
                    .message("Payment declined — insufficient funds (simulated)")
                    .build();
        }
    }

    @Override
    public PaymentResponse processRefund(PaymentRequest request) {
        log.info("[MockPaymentGateway] Processing refund of {} for booking {}",
                request.getAmount(), request.getBookingId());

        simulateDelay();

        String transactionRef = "REFUND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return PaymentResponse.builder()
                .paymentId(UUID.randomUUID().toString())
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .status("REFUNDED")
                .transactionReference(transactionRef)
                .message("Refund processed successfully")
                .build();
    }

    private void simulateDelay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
