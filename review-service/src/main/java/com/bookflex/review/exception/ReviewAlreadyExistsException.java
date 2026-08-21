package com.bookflex.review.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ReviewAlreadyExistsException extends BaseException {
    public ReviewAlreadyExistsException(UUID bookingId) {
        super("A review has already been submitted for booking: " + bookingId,
                HttpStatus.CONFLICT,
                "REVIEW_ALREADY_EXISTS");
    }
}
