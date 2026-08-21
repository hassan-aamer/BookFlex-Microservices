package com.bookflex.review.client;

import com.bookflex.review.dto.BookingClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for inter-service communication with booking-service.
 */
@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/api/bookings/{id}")
    BookingClientDto getBookingById(@PathVariable("id") String bookingId);
}
