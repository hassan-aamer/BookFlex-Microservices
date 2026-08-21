package com.bookflex.resource.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceId) {
        super(
                "Resource not found: " + resourceId,
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND"
        );
    }
}
