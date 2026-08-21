package com.bookflex.booking.mapper;

import com.bookflex.booking.dto.BookingResponse;
import com.bookflex.booking.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(BookingEntity entity) {
        return BookingResponse.builder()
                .id(entity.getId().toString())
                .resourceId(entity.getResourceId())
                .resourceName(entity.getResourceName())
                .resourceType(entity.getResourceType())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomerName())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus().name())
                .cancellationPolicy(entity.getCancellationPolicy())
                .paymentId(entity.getPaymentId())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .confirmedAt(entity.getConfirmedAt())
                .cancelledAt(entity.getCancelledAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}
