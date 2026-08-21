package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.exception.InvalidBookingStateException;

/**
 * State Pattern — COMPLETED state (terminal).
 *
 * <p>A completed booking represents a successfully fulfilled reservation.
 * No further transitions are allowed.</p>
 */
public class CompletedState implements BookingState {

    @Override
    public void confirm(BookingEntity booking) {
        throw new InvalidBookingStateException("Cannot confirm a COMPLETED booking.");
    }

    @Override
    public void cancel(BookingEntity booking) {
        throw new InvalidBookingStateException("Cannot cancel a COMPLETED booking.");
    }

    @Override
    public void complete(BookingEntity booking) {
        throw new InvalidBookingStateException("Booking is already COMPLETED.");
    }

    @Override
    public String getStateName() {
        return "COMPLETED";
    }
}
