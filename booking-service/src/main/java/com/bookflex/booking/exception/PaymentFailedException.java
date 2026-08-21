package com.bookflex.booking.exception;

import com.bookflex.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentFailedException extends BaseException {
    public PaymentFailedException(String bookingId, String reason) {
        super("Payment failed for booking '" + bookingId + "': " + reason,
                HttpStatus.PAYMENT_REQUIRED, "PAYMENT_FAILED");
    }
}
