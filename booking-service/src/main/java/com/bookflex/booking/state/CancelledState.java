package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.exception.InvalidBookingStateException;

/**
 * State Pattern — CANCELLED state (terminal).
 *
 * <p>A cancelled booking cannot transition to any other state.
 * All transition methods throw {@link InvalidBookingStateException}.</p>
 */
public class CancelledState implements BookingState {

    @Override
    public void confirm(BookingEntity booking) {
        throw new InvalidBookingStateException("Cannot confirm a CANCELLED booking.");
    }

    @Override
    public void cancel(BookingEntity booking) {
        throw new InvalidBookingStateException("Booking is already CANCELLED.");
    }

    @Override
    public void complete(BookingEntity booking) {
        throw new InvalidBookingStateException("Cannot complete a CANCELLED booking.");
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}
