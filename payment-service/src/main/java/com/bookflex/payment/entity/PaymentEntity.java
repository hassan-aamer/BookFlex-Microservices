package com.bookflex.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "booking_id", nullable = false)
    private String bookingId;
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    @Column(name = "payment_method", length = 30)
    private String paymentMethod;
    @Column(name = "transaction_reference")
    private String transactionReference;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
