package com.bookflex.notification.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Email notification sender (mock — logs instead of actually sending).
 *
 * <p>In production, this would integrate with JavaMail, SendGrid, or similar.
 * The important thing is that swapping implementations is a bean configuration
 * change, not a code change (DIP).</p>
 */
@Slf4j
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("═══════════════════════════════════════════════════════════════");
        log.info("📧 [EMAIL] To: {}", recipient);
        log.info("📧 [EMAIL] Subject: {}", subject);
        log.info("📧 [EMAIL] Body: {}", body);
        log.info("═══════════════════════════════════════════════════════════════");
    }

    @Override
    public String getChannel() {
        return "EMAIL";
    }
}
