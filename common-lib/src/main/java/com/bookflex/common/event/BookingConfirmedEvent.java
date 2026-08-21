package com.bookflex.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a booking is successfully confirmed.
 * Consumed by notification-service and resource-service via RabbitMQ.
 *
 * <p><b>Observer Pattern (inter-service)</b>: This event decouples the booking-service
 * from the notification and resource services. The publisher does not know or care
 * who consumes this event, enabling independent evolution of each service.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmedEvent implements Serializable {

    private String bookingId;
    private String resourceId;
    private String resourceName;
    private String customerId;
    private String customerEmail;
    private String customerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private LocalDateTime confirmedAt;
}
