package com.bookflex.booking.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidBookingStateException extends BaseException {
    public InvalidBookingStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_BOOKING_STATE");
    }
}
