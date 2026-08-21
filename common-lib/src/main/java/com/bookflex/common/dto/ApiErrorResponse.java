package com.bookflex.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized API error response used across all microservices.
 * Ensures a uniform error contract for clients regardless of which service returns the error.
 *
 * <p><b>SRP (Single Responsibility)</b>: This class has one job — represent an API error.
 * It is decoupled from exception handling logic (which lives in each service's GlobalExceptionHandler).</p>
 */
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String path;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    private final List<FieldValidationError> validationErrors;

    /**
     * Represents a single field-level validation error (e.g., "email must not be blank").
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class FieldValidationError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
    }
}
