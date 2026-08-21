package com.bookflex.review.controller;

import com.bookflex.review.dto.CreateReviewRequest;
import com.bookflex.review.dto.RatingSummaryResponse;
import com.bookflex.review.dto.ReviewResponse;
import com.bookflex.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for verified review management.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Verified customer rating & review management")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a verified review (Customer)")
    public ResponseEntity<ReviewResponse> submitReview(
            @Valid @RequestBody CreateReviewRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String customerId,
            @RequestHeader(value = "X-User-Email", required = false) String customerEmail) {

        if (customerId == null) customerId = "anonymous";
        if (customerEmail == null) customerEmail = "customer@bookflex.com";

        ReviewResponse response = reviewService.submitReview(request, customerId, customerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get all reviews for a resource")
    public ResponseEntity<List<ReviewResponse>> getReviewsByResource(@PathVariable UUID resourceId) {
        return ResponseEntity.ok(reviewService.getReviewsByResource(resourceId));
    }

    @GetMapping("/resource/{resourceId}/summary")
    @Operation(summary = "Get average rating and total reviews summary for a resource")
    public ResponseEntity<RatingSummaryResponse> getResourceRatingSummary(@PathVariable UUID resourceId) {
        return ResponseEntity.ok(reviewService.getResourceRatingSummary(resourceId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) String customerId) {

        if (customerId == null) customerId = "anonymous";
        reviewService.deleteReview(id, customerId);
        return ResponseEntity.noContent().build();
    }
}
