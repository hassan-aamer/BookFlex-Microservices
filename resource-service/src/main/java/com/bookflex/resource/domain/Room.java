package com.bookflex.resource.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A hotel room or rental accommodation — concrete implementation of {@link BookableResource}.
 *
 * <p><b>Inheritance + Polymorphism (OOP Pillars)</b>: Room inherits the BookableResource
 * contract and provides room-specific behavior. When the booking-service calls
 * {@code resource.isAvailableAt(slot)}, it gets room-specific availability logic
 * without knowing it's dealing with a Room (polymorphism).</p>
 *
 * <p><b>Liskov Substitution</b>: A Room can be used anywhere a BookableResource is expected.
 * It honors the contract: minimum duration is 1 night (24 hours), and availability
 * checks against its booked time slots.</p>
 */
@Getter
@Builder
public class Room implements BookableResource {

    private final String resourceId;
    private final String roomNumber;
    private final int capacity;
    private final int floor;
    private final List<String> amenities;
    private final String description;

    /**
     * Set of time slots that are already booked.
     * Using a Set guarantees no duplicate bookings (Collections Framework).
     */
    @Builder.Default
    private final Set<TimeSlot> bookedSlots = new HashSet<>();

    @Override
    public ResourceType getType() {
        return ResourceType.ROOM;
    }

    /**
     * Rooms have a minimum booking duration of 1 night (24 hours).
     */
    @Override
    public Duration getMinBookingDuration() {
        return Duration.ofHours(24);
    }

    /**
     * A room is available if the requested slot does not overlap with any existing booking.
     * Uses Stream API for clear, functional-style filtering.
     */
    @Override
    public boolean isAvailableAt(TimeSlot slot) {
        return bookedSlots.stream().noneMatch(booked -> booked.overlapsWith(slot));
    }

    @Override
    public String getDisplayName() {
        return "Room " + roomNumber + " (Floor " + floor + ", Capacity: " + capacity + ")";
    }
}
