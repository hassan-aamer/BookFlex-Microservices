package com.bookflex.review.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidBookingException extends BaseException {
    public InvalidBookingException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_BOOKING_FOR_REVIEW");
    }
}
