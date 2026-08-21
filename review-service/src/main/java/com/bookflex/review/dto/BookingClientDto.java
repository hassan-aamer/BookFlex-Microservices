package com.bookflex.review.dto;

import lombok.*;

/**
 * Feign response mapping for booking information retrieved from booking-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingClientDto {
    private String id;
    private String resourceId;
    private String customerId;
    private String customerName;
    private String status;
}
