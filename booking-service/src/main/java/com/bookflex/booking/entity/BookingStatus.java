package com.bookflex.booking.entity;

/**
 * Booking lifecycle statuses.
 * Used with the State Pattern — each status maps to a concrete BookingState implementation.
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
