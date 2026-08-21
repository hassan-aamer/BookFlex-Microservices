package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingStatus;

/**
 * Resolves the correct {@link BookingState} implementation for a given {@link BookingStatus}.
 *
 * <p>This is a utility class that maps enum values to State Pattern objects.
 * It centralizes the mapping so that the service layer doesn't need to know
 * about concrete state classes.</p>
 */
public final class BookingStateResolver {

    private BookingStateResolver() {
        // Utility class — not instantiable
    }

    private static final PendingState PENDING = new PendingState();
    private static final ConfirmedState CONFIRMED = new ConfirmedState();
    private static final CancelledState CANCELLED = new CancelledState();
    private static final CompletedState COMPLETED = new CompletedState();

    /**
     * Returns the appropriate BookingState for the given status.
     */
    public static BookingState resolve(BookingStatus status) {
        return switch (status) {
            case PENDING -> PENDING;
            case CONFIRMED -> CONFIRMED;
            case CANCELLED -> CANCELLED;
            case COMPLETED -> COMPLETED;
        };
    }
}
