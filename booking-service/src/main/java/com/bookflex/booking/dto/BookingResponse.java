package com.bookflex.booking.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BookingResponse {
    private String id;
    private String resourceId;
    private String resourceName;
    private String resourceType;
    private String customerId;
    private String customerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private String status;
    private String cancellationPolicy;
    private String paymentId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
}
