package com.bookflex.review.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ReviewNotFoundException extends BaseException {
    public ReviewNotFoundException(UUID reviewId) {
        super("Review not found with ID: " + reviewId, HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND");
    }
}
