package com.bookflex.review.mapper;

import com.bookflex.review.dto.CreateReviewRequest;
import com.bookflex.review.dto.ReviewResponse;
import com.bookflex.review.entity.ReviewEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper converting between ReviewEntity and Review DTOs.
 */
@Component
public class ReviewMapper {

    public ReviewEntity toEntity(CreateReviewRequest request, String customerId, String customerName) {
        return ReviewEntity.builder()
                .resourceId(request.getResourceId())
                .bookingId(request.getBookingId())
                .customerId(customerId)
                .customerName(customerName != null ? customerName : "Customer")
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    public ReviewResponse toResponse(ReviewEntity entity) {
        return ReviewResponse.builder()
                .id(entity.getId())
                .resourceId(entity.getResourceId())
                .bookingId(entity.getBookingId())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomerName())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
