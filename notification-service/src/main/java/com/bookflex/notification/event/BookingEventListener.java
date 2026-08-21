package com.bookflex.notification.event;

import com.bookflex.common.constant.RabbitMQConstants;
import com.bookflex.common.event.BookingCancelledEvent;
import com.bookflex.common.event.BookingConfirmedEvent;
import com.bookflex.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern — consumes booking events from RabbitMQ and sends notifications.
 *
 * <p><b>Why Observer here?</b>
 * The notification-service doesn't poll the booking-service for changes.
 * Instead, it subscribes to booking events via RabbitMQ and reacts automatically.
 * This is the consumer side of the Observer Pattern implemented across services.</p>
 *
 * <p>Adding a new event type (e.g., BookingCompletedEvent) requires only:
 * <ol>
 *   <li>Create the event class in common-lib</li>
 *   <li>Add a publisher in booking-service</li>
 *   <li>Add a @RabbitListener here</li>
 * </ol>
 * No existing code changes needed (OCP).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConstants.BOOKING_CONFIRMED_QUEUE)
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        log.info("[Observer] Received BookingConfirmedEvent for booking: {}", event.getBookingId());

        String subject = "Booking Confirmed — " + event.getResourceName();
        String body = String.format(
                "Dear %s,\n\nYour booking has been confirmed!\n\n" +
                "📋 Booking ID: %s\n" +
                "🏷 Resource: %s\n" +
                "📅 Date: %s to %s\n" +
                "💰 Amount: $%s\n\n" +
                "Thank you for using BookFlex!",
                event.getCustomerName(),
                event.getBookingId(),
                event.getResourceName(),
                event.getStartTime(),
                event.getEndTime(),
                event.getTotalAmount()
        );

        notificationService.sendNotification(
                event.getCustomerId(),
                event.getCustomerEmail(),
                "BOOKING_CONFIRMED",
                subject,
                body,
                "EMAIL"
        );
    }

    @RabbitListener(queues = RabbitMQConstants.BOOKING_CANCELLED_QUEUE)
    public void handleBookingCancelled(BookingCancelledEvent event) {
        log.info("[Observer] Received BookingCancelledEvent for booking: {}", event.getBookingId());

        String subject = "Booking Cancelled — " + event.getResourceName();
        String body = String.format(
                "Dear %s,\n\nYour booking has been cancelled.\n\n" +
                "📋 Booking ID: %s\n" +
                "🏷 Resource: %s\n" +
                "📅 Date: %s to %s\n" +
                "💸 Refund: $%s\n" +
                "📝 Reason: %s\n\n" +
                "If you have questions, please contact support.",
                event.getCustomerName(),
                event.getBookingId(),
                event.getResourceName(),
                event.getStartTime(),
                event.getEndTime(),
                event.getRefundAmount(),
                event.getCancellationReason()
        );

        notificationService.sendNotification(
                event.getCustomerId(),
                event.getCustomerEmail(),
                "BOOKING_CANCELLED",
                subject,
                body,
                "EMAIL"
        );
    }
}
