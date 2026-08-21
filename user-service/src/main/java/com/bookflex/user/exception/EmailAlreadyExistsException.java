package com.bookflex.user.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a user attempts to register with an email that already exists.
 */
public class EmailAlreadyExistsException extends BaseException {

    public EmailAlreadyExistsException(String email) {
        super(
                "A user with email '" + email + "' already exists",
                HttpStatus.CONFLICT,
                "USER_EMAIL_DUPLICATE"
        );
    }
}
