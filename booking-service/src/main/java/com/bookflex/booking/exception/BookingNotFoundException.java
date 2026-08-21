package com.bookflex.booking.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends BaseException {
    public BookingNotFoundException(String bookingId) {
        super("Booking not found: " + bookingId, HttpStatus.NOT_FOUND, "BOOKING_NOT_FOUND");
    }
}
