package com.bookflex.booking.controller;

import com.bookflex.booking.dto.BookingResponse;
import com.bookflex.booking.dto.CancelBookingRequest;
import com.bookflex.booking.dto.CreateBookingRequest;
import com.bookflex.booking.service.BookingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST controller for booking operations.
 *
 * <p><b>Thin Controller (SRP)</b>: Delegates all logic to BookingServiceImpl.
 * Customer ID is extracted from the gateway-forwarded X-User-Id header.</p>
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking lifecycle management")
public class BookingController {

    private final BookingServiceImpl bookingService;

    @PostMapping
    @Operation(summary = "Create a booking (Customer)")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String customerId) {
        if (customerId == null) customerId = "anonymous";
        BookingResponse response = bookingService.createBooking(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current customer's bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @RequestHeader(value = "X-User-Id", required = false) String customerId) {
        if (customerId == null) customerId = "anonymous";
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(customerId));
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get bookings for a resource (Provider)")
    public ResponseEntity<List<BookingResponse>> getBookingsByResource(
            @PathVariable String resourceId) {
        return ResponseEntity.ok(bookingService.getBookingsByResource(resourceId));
    }

    @GetMapping("/resource/{resourceId}/by-date")
    @Operation(summary = "Get bookings grouped by date (Collections Framework demo)")
    public ResponseEntity<Map<LocalDate, List<BookingResponse>>> getBookingsGroupedByDate(
            @PathVariable String resourceId) {
        return ResponseEntity.ok(bookingService.getBookingsByResourceGroupedByDate(resourceId));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking with refund calculation")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable String id,
            @RequestBody(required = false) CancelBookingRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String customerId) {
        if (request == null) request = new CancelBookingRequest();
        if (customerId == null) customerId = "anonymous";
        return ResponseEntity.ok(bookingService.cancelBooking(id, request, customerId));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete a booking")
    public ResponseEntity<BookingResponse> completeBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.completeBooking(id));
    }
}
