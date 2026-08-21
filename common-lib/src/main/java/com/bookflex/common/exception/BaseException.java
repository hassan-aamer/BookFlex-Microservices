package com.bookflex.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all BookFlex domain exceptions.
 * Each microservice extends this to create its own specific exceptions.
 *
 * <p><b>Template Method influence</b>: By defining a common structure (status + errorCode + message),
 * all service-level exception handlers can process any BookFlex exception uniformly,
 * while each subclass customizes the specific error details.</p>
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    protected BaseException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
