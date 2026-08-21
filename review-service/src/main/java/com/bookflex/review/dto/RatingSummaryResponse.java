package com.bookflex.review.dto;

import lombok.*;

import java.util.UUID;

/**
 * Aggregated rating summary for a resource (average star rating & total count).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingSummaryResponse {

    private UUID resourceId;
    private Double averageRating;
    private Long totalReviews;
}
