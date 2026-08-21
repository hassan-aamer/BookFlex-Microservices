package com.bookflex.user.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested user cannot be found by ID or email.
 */
public class UserNotFoundException extends BaseException {

    public UserNotFoundException(String identifier) {
        super(
                "User not found: " + identifier,
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }
}
