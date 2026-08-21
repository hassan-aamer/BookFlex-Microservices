package com.bookflex.booking.state;

import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.entity.BookingStatus;
import com.bookflex.booking.exception.InvalidBookingStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    @DisplayName("State Pattern: PENDING state allows confirm and cancel, disallows complete")
    void testPendingStateTransitions() {
        BookingState pendingState = BookingStateResolver.resolve(BookingStatus.PENDING);
        BookingEntity booking = BookingEntity.builder().status(BookingStatus.PENDING).build();

        // Confirm
        pendingState.confirm(booking);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        // Cancel
        booking.setStatus(BookingStatus.PENDING);
        pendingState.cancel(booking);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());

        // Complete (invalid from PENDING)
        booking.setStatus(BookingStatus.PENDING);
        assertThrows(InvalidBookingStateException.class, () -> pendingState.complete(booking));
    }

    @Test
    @DisplayName("State Pattern: CONFIRMED state allows complete and cancel, disallows re-confirm")
    void testConfirmedStateTransitions() {
        BookingState confirmedState = BookingStateResolver.resolve(BookingStatus.CONFIRMED);
        BookingEntity booking = BookingEntity.builder().status(BookingStatus.CONFIRMED).build();

        // Complete
        confirmedState.complete(booking);
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());

        // Re-confirm (invalid)
        booking.setStatus(BookingStatus.CONFIRMED);
        assertThrows(InvalidBookingStateException.class, () -> confirmedState.confirm(booking));
    }

    @Test
    @DisplayName("State Pattern: CANCELLED terminal state throws on all transitions")
    void testCancelledStateTransitions() {
        BookingState cancelledState = BookingStateResolver.resolve(BookingStatus.CANCELLED);
        BookingEntity booking = BookingEntity.builder().status(BookingStatus.CANCELLED).build();

        assertThrows(InvalidBookingStateException.class, () -> cancelledState.confirm(booking));
        assertThrows(InvalidBookingStateException.class, () -> cancelledState.cancel(booking));
        assertThrows(InvalidBookingStateException.class, () -> cancelledState.complete(booking));
    }
}
