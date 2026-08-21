package com.bookflex.booking.client;

import com.bookflex.common.dto.PaymentRequest;
import com.bookflex.common.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for communicating with payment-service.
 */
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    @PostMapping("/api/payments/refund")
    PaymentResponse processRefund(@RequestBody PaymentRequest request);
}
