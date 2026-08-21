package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.entity.BookingStatus;
import com.bookflex.booking.exception.InvalidBookingStateException;

/**
 * State Pattern — CONFIRMED state.
 *
 * <p><b>Allowed transitions from CONFIRMED:</b>
 * <ul>
 *   <li>{@code complete()} → COMPLETED (booking successfully fulfilled)</li>
 *   <li>{@code cancel()} → CANCELLED (late cancellation — may trigger partial/no refund)</li>
 * </ul>
 * </p>
 *
 * <p><b>Disallowed:</b> {@code confirm()} — already confirmed, cannot re-confirm.</p>
 */
public class ConfirmedState implements BookingState {

    @Override
    public void confirm(BookingEntity booking) {
        throw new InvalidBookingStateException("Booking is already CONFIRMED.");
    }

    @Override
    public void cancel(BookingEntity booking) {
        booking.setStatus(BookingStatus.CANCELLED);
    }

    @Override
    public void complete(BookingEntity booking) {
        booking.setStatus(BookingStatus.COMPLETED);
    }

    @Override
    public String getStateName() {
        return "CONFIRMED";
    }
}
