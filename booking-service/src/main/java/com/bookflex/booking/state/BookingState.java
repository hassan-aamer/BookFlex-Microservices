package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingEntity;

/**
 * State Pattern — defines the contract for booking state transitions.
 *
 * <p><b>Why State Pattern here?</b>
 * A booking's behavior changes dramatically based on its current status:
 * - A PENDING booking can be confirmed or cancelled, but not completed.
 * - A CONFIRMED booking can be completed or cancelled, but not re-confirmed.
 * - CANCELLED and COMPLETED are terminal states — no further transitions.
 *
 * Using an enum with switch/case would scatter transition logic across the service layer,
 * violating both SRP and OCP. The State Pattern encapsulates each state's behavior
 * in its own class, making transitions explicit and impossible to miss.</p>
 *
 * <p><b>Key benefit</b>: Adding a new state (e.g., REFUND_PENDING) requires only creating
 * a new class — existing states don't need modification (Open/Closed Principle).</p>
 */
public interface BookingState {

    /**
     * Attempts to transition the booking to the CONFIRMED state.
     *
     * @param booking the booking entity to transition
     * @throws com.bookflex.booking.exception.InvalidBookingStateException if this transition is not allowed
     */
    void confirm(BookingEntity booking);

    /**
     * Attempts to transition the booking to the CANCELLED state.
     *
     * @param booking the booking entity to transition
     * @throws com.bookflex.booking.exception.InvalidBookingStateException if this transition is not allowed
     */
    void cancel(BookingEntity booking);

    /**
     * Attempts to transition the booking to the COMPLETED state.
     *
     * @param booking the booking entity to transition
     * @throws com.bookflex.booking.exception.InvalidBookingStateException if this transition is not allowed
     */
    void complete(BookingEntity booking);

    /**
     * Returns the name of this state for display/logging purposes.
     */
    String getStateName();
}
