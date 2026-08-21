package com.bookflex.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Payment request DTO sent from booking-service to payment-service.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private String bookingId;
    private String customerId;
    private BigDecimal amount;
    private String paymentMethod;
    private String description;
}
