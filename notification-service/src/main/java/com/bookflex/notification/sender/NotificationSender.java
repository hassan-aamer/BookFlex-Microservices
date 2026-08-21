package com.bookflex.notification.sender;

/**
 * Notification Sender interface — defines the contract for sending notifications.
 *
 * <p><b>ISP (Interface Segregation Principle)</b>:
 * This interface is focused solely on sending notifications. It does NOT handle:
 * <ul>
 *   <li>Payment processing (that's {@code PaymentGateway})</li>
 *   <li>Cancellation policy calculation (that's {@code CancellationPolicy})</li>
 *   <li>Booking state management (that's {@code BookingState})</li>
 * </ul>
 *
 * Each of these is a separate, focused interface. Classes implementing
 * {@code NotificationSender} don't need to implement payment or cancellation
 * methods they don't care about — they only implement {@code send()}.</p>
 *
 * <p><b>DIP</b>: The notification service depends on this interface, not on
 * concrete senders. New channels (SMS, push notifications) can be added
 * by creating new implementations without changing existing code.</p>
 */
public interface NotificationSender {

    /**
     * Sends a notification to a recipient.
     *
     * @param recipient the target (email address, phone number, etc.)
     * @param subject   notification subject/title
     * @param body      notification body/content
     */
    void send(String recipient, String subject, String body);

    /**
     * Returns the channel name for this sender (e.g., "EMAIL", "LOG", "SMS").
     */
    String getChannel();
}
