package com.bookflex.notification.service;

import com.bookflex.notification.entity.NotificationEntity;
import com.bookflex.notification.repository.NotificationRepository;
import com.bookflex.notification.sender.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Notification service — orchestrates sending through available channels.
 *
 * <p><b>DIP + ISP</b>: Depends on the {@link NotificationSender} interface.
 * All available senders are auto-discovered by Spring and stored in a lookup map.
 * Adding a new channel (e.g., SMS) requires only creating a new @Component
 * implementing NotificationSender — no changes here.</p>
 */
@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Map<String, NotificationSender> senderMap;

    /**
     * Constructor Injection: Spring injects ALL NotificationSender implementations.
     */
    public NotificationService(NotificationRepository notificationRepository,
                               List<NotificationSender> senders) {
        this.notificationRepository = notificationRepository;
        this.senderMap = senders.stream()
                .collect(Collectors.toMap(NotificationSender::getChannel, Function.identity()));
        log.info("Registered notification channels: {}", senderMap.keySet());
    }

    @Transactional
    public void sendNotification(String userId, String recipient, String type,
                                 String subject, String body, String channel) {
        NotificationSender sender = senderMap.getOrDefault(channel, senderMap.get("LOG"));

        sender.send(recipient, subject, body);

        NotificationEntity entity = NotificationEntity.builder()
                .userId(userId)
                .type(type)
                .channel(sender.getChannel())
                .recipient(recipient)
                .subject(subject)
                .message(body)
                .status("SENT")
                .build();

        notificationRepository.save(entity);
    }

    public List<NotificationEntity> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    public NotificationEntity getNotificationById(String notificationId) {
        return notificationRepository.findById(java.util.UUID.fromString(notificationId))
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
    }
}
