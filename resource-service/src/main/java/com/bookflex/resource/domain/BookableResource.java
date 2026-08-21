package com.bookflex.resource.domain;

import java.time.Duration;

/**
 * Core abstraction for any resource that can be booked in the system.
 *
 * <p><b>Abstraction (OOP Pillar)</b>: This interface defines the essential contract
 * for a bookable resource without exposing implementation details. Whether it's a hotel room,
 * a doctor's appointment, or a sports field, any consumer (particularly the booking-service)
 * interacts only through this interface.</p>
 *
 * <p><b>Open/Closed Principle</b>: New resource types can be added by implementing this
 * interface without modifying existing code. The booking-service depends on this interface,
 * not on concrete types like {@code Room} or {@code AppointmentSlot}.</p>
 *
 * <p><b>Liskov Substitution Principle</b>: Any implementation of this interface must be
 * safely substitutable wherever {@code BookableResource} is expected. Each implementation
 * must honor the contract: {@code isAvailableAt()} must accurately reflect availability,
 * and {@code getMinBookingDuration()} must return a valid positive duration.</p>
 *
 * <p><b>Interface Segregation Principle</b>: This interface is focused and minimal —
 * it contains only the methods needed for booking operations. Resource-specific concerns
 * (like room amenities or doctor specialties) are handled by the concrete implementations.</p>
 */
public interface BookableResource {

    /**
     * Returns the unique identifier for this resource.
     */
    String getResourceId();

    /**
     * Returns the type/category of this resource.
     */
    ResourceType getType();

    /**
     * Returns the minimum duration for which this resource can be booked.
     * For example, a room might require minimum 1 night, an appointment minimum 30 minutes.
     */
    Duration getMinBookingDuration();

    /**
     * Checks whether this resource is available during the specified time slot.
     *
     * @param slot the time window to check
     * @return true if the resource is available for the entire duration of the slot
     */
    boolean isAvailableAt(TimeSlot slot);

    /**
     * Returns a human-readable name/description for this resource.
     */
    String getDisplayName();
}
