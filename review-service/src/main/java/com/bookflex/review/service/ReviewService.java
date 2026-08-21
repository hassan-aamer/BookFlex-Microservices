package com.bookflex.review.service;

import com.bookflex.review.client.BookingClient;
import com.bookflex.review.dto.*;
import com.bookflex.review.entity.ReviewEntity;
import com.bookflex.review.exception.InvalidBookingException;
import com.bookflex.review.exception.ReviewAlreadyExistsException;
import com.bookflex.review.exception.ReviewNotFoundException;
import com.bookflex.review.mapper.ReviewMapper;
import com.bookflex.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service managing customer reviews, verified booking checks, and rating aggregations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BookingClient bookingClient;

    @Transactional
    public ReviewResponse submitReview(CreateReviewRequest request, String customerId, String customerName) {
        // 1. Check for duplicate review on same booking
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new ReviewAlreadyExistsException(request.getBookingId());
        }

        // 2. Verify booking existence & customer ownership via Feign call
        BookingClientDto booking;
        try {
            booking = bookingClient.getBookingById(request.getBookingId().toString());
        } catch (Exception e) {
            log.error("Failed to fetch booking {}: {}", request.getBookingId(), e.getMessage());
            throw new InvalidBookingException("Booking not found: " + request.getBookingId());
        }

        // Verify booking belongs to this customer
        if (!booking.getCustomerId().equals(customerId)) {
            throw new InvalidBookingException("Customer is not authorized to review this booking");
        }

        // Verify resource ID matches
        if (!booking.getResourceId().equalsIgnoreCase(request.getResourceId().toString())) {
            throw new InvalidBookingException("Booking resource does not match requested review resource");
        }

        // 3. Save review entity
        ReviewEntity entity = reviewMapper.toEntity(request, customerId,
                booking.getCustomerName() != null ? booking.getCustomerName() : customerName);
        ReviewEntity saved = reviewRepository.save(entity);
        log.info("Review created: {} for resource: {} with rating: {} stars", saved.getId(), saved.getResourceId(), saved.getRating());

        return reviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByResource(UUID resourceId) {
        return reviewRepository.findByResourceIdOrderByCreatedAtDesc(resourceId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RatingSummaryResponse getResourceRatingSummary(UUID resourceId) {
        Double avg = reviewRepository.calculateAverageRatingForResource(resourceId);
        long count = reviewRepository.countByResourceId(resourceId);

        double roundedAvg = (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0;

        return RatingSummaryResponse.builder()
                .resourceId(resourceId)
                .averageRating(roundedAvg)
                .totalReviews(count)
                .build();
    }

    @Transactional
    public void deleteReview(UUID reviewId, String customerId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getCustomerId().equals(customerId)) {
            throw new InvalidBookingException("Only the review author can delete this review");
        }

        reviewRepository.delete(review);
        log.info("Review deleted: {}", reviewId);
    }
}
