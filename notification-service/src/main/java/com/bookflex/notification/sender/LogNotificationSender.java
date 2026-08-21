package com.bookflex.notification.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Log notification sender — writes notifications to the application log.
 * Useful for development and testing where actual email delivery isn't needed.
 */
@Slf4j
@Component
public class LogNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("[LOG-NOTIFICATION] → {} | {} | {}", recipient, subject, body);
    }

    @Override
    public String getChannel() {
        return "LOG";
    }
}
