package com.bookflex.booking.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyBookedException extends BaseException {
    public ResourceAlreadyBookedException(String resourceId, String timeSlot) {
        super("Resource '" + resourceId + "' is already booked for time slot " + timeSlot,
                HttpStatus.CONFLICT, "RESOURCE_ALREADY_BOOKED");
    }
}
