package com.bookflex.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * Request payload for submitting a verified resource review.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    @NotNull(message = "Resource ID is required")
    private UUID resourceId;

    @NotNull(message = "Booking ID is required")
    private UUID bookingId;

    @NotNull(message = "Rating score is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private Integer rating;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}
