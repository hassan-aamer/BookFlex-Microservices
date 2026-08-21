package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.entity.BookingStatus;

/**
 * State Pattern — PENDING state.
 *
 * <p><b>Allowed transitions from PENDING:</b>
 * <ul>
 *   <li>{@code confirm()} → CONFIRMED (payment successful)</li>
 *   <li>{@code cancel()} → CANCELLED (user cancels or payment fails)</li>
 * </ul>
 * </p>
 *
 * <p><b>Disallowed:</b> {@code complete()} — a booking cannot be completed
 * without being confirmed first.</p>
 */
public class PendingState implements BookingState {

    @Override
    public void confirm(BookingEntity booking) {
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    @Override
    public void cancel(BookingEntity booking) {
        booking.setStatus(BookingStatus.CANCELLED);
    }

    @Override
    public void complete(BookingEntity booking) {
        throw new com.bookflex.booking.exception.InvalidBookingStateException(
                "Cannot complete a booking that is still PENDING. It must be CONFIRMED first.");
    }

    @Override
    public String getStateName() {
        return "PENDING";
    }
}
