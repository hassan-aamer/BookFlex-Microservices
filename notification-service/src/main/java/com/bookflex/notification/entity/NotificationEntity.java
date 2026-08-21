package com.bookflex.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    @Column(name = "channel", nullable = false, length = 20)
    private String channel;
    @Column(name = "recipient")
    private String recipient;
    @Column(name = "subject", length = 300)
    private String subject;
    @Column(name = "message", length = 2000)
    private String message;
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "SENT";
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
    @PrePersist
    protected void onCreate() { if (this.sentAt == null) this.sentAt = LocalDateTime.now(); }
}
