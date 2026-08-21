package com.bookflex.common.constant;

/**
 * RabbitMQ exchange, queue, and routing key constants shared across services.
 * Centralizing these prevents typo-driven wiring bugs between publisher and consumer.
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {
        // Utility class — not instantiable
    }

    // ── Exchanges ──────────────────────────────────────────────────────────
    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    // ── Queues ─────────────────────────────────────────────────────────────
    public static final String BOOKING_CONFIRMED_QUEUE = "booking.confirmed.queue";
    public static final String BOOKING_CANCELLED_QUEUE = "booking.cancelled.queue";
    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.queue";

    // Notification-specific queues
    public static final String NOTIFICATION_BOOKING_CONFIRMED_QUEUE = "notification.booking.confirmed.queue";
    public static final String NOTIFICATION_BOOKING_CANCELLED_QUEUE = "notification.booking.cancelled.queue";

    // Resource-specific queues
    public static final String RESOURCE_BOOKING_CONFIRMED_QUEUE = "resource.booking.confirmed.queue";
    public static final String RESOURCE_BOOKING_CANCELLED_QUEUE = "resource.booking.cancelled.queue";

    // ── Routing Keys ───────────────────────────────────────────────────────
    public static final String BOOKING_CONFIRMED_ROUTING_KEY = "booking.confirmed";
    public static final String BOOKING_CANCELLED_ROUTING_KEY = "booking.cancelled";
    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";
}
