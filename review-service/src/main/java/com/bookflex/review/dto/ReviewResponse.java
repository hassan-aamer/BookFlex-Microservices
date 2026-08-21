package com.bookflex.review.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload for a review.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private UUID id;
    private UUID resourceId;
    private String customerId;
    private String customerName;
    private UUID bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
