package com.bookflex.user.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when login credentials are invalid (wrong email or password).
 */
public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super(
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED,
                "AUTH_INVALID_CREDENTIALS"
        );
    }
}
