package com.bookflex.payment.service;

import com.bookflex.common.dto.PaymentRequest;
import com.bookflex.common.dto.PaymentResponse;
import com.bookflex.payment.entity.PaymentEntity;
import com.bookflex.payment.gateway.PaymentGateway;
import com.bookflex.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment service — delegates to PaymentGateway and persists results.
 *
 * <p><b>DIP</b>: Depends on the {@link PaymentGateway} interface, not on
 * {@code MockPaymentGateway} directly. The concrete implementation is
 * injected by Spring's DI container via constructor injection.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = paymentGateway.processPayment(request);

        PaymentEntity entity = PaymentEntity.builder()
                .bookingId(request.getBookingId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .status(response.getStatus())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(response.getTransactionReference())
                .description(request.getDescription())
                .build();

        paymentRepository.save(entity);
        response.setPaymentId(entity.getId().toString());

        return response;
    }

    @Transactional
    public PaymentResponse processRefund(PaymentRequest request) {
        PaymentResponse response = paymentGateway.processRefund(request);

        PaymentEntity entity = PaymentEntity.builder()
                .bookingId(request.getBookingId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .status(response.getStatus())
                .paymentMethod("REFUND")
                .transactionReference(response.getTransactionReference())
                .description(request.getDescription())
                .build();

        paymentRepository.save(entity);
        response.setPaymentId(entity.getId().toString());

        return response;
    }

    public PaymentResponse getPaymentById(String paymentId) {
        PaymentEntity entity = paymentRepository.findById(java.util.UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return toResponse(entity);
    }

    public java.util.List<PaymentResponse> getPaymentsByBooking(String bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::toResponse).toList();
    }

    private PaymentResponse toResponse(PaymentEntity entity) {
        return PaymentResponse.builder()
                .paymentId(entity.getId().toString())
                .bookingId(entity.getBookingId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .transactionReference(entity.getTransactionReference())
                .build();
    }
}
