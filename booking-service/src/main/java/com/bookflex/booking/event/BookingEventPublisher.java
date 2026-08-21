package com.bookflex.booking.event;

import com.bookflex.common.constant.RabbitMQConstants;
import com.bookflex.common.event.BookingCancelledEvent;
import com.bookflex.common.event.BookingConfirmedEvent;
import com.bookflex.common.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern — publishes booking events to RabbitMQ.
 *
 * <p><b>Why Observer Pattern here?</b>
 * When a booking changes state, multiple services need to react:
 * - notification-service sends an email/SMS
 * - resource-service updates availability
 * - payment-service processes refunds
 *
 * Without Observer, the booking-service would need direct dependencies on all consumers,
 * creating tight coupling. With RabbitMQ as the event bus, the publisher doesn't know
 * or care who listens — services can be added/removed independently.</p>
 *
 * <p><b>@Async</b>: Event publishing is asynchronous to avoid slowing down the
 * main booking response. If RabbitMQ is temporarily unavailable, the booking
 * still succeeds — events can be retried via a dead-letter queue.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void publishBookingCreated(BookingCreatedEvent event) {
        log.info("[Observer] Publishing BookingCreatedEvent for booking: {}", event.getBookingId());
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.BOOKING_EXCHANGE,
                RabbitMQConstants.BOOKING_CREATED_ROUTING_KEY,
                event);
    }

    @Async
    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        log.info("[Observer] Publishing BookingConfirmedEvent for booking: {}", event.getBookingId());
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.BOOKING_EXCHANGE,
                RabbitMQConstants.BOOKING_CONFIRMED_ROUTING_KEY,
                event);
    }

    @Async
    public void publishBookingCancelled(BookingCancelledEvent event) {
        log.info("[Observer] Publishing BookingCancelledEvent for booking: {}", event.getBookingId());
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.BOOKING_EXCHANGE,
                RabbitMQConstants.BOOKING_CANCELLED_ROUTING_KEY,
                event);
    }
}
