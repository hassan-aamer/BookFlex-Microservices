package com.bookflex.resource.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * A sports field (football pitch, tennis court, etc.) — concrete implementation of {@link BookableResource}.
 *
 * <p><b>Open/Closed Principle — PROOF</b>: This class was added AFTER the booking-service
 * was fully implemented. Adding it required:
 * <ol>
 *   <li>Create this class (implements BookableResource)</li>
 *   <li>Add SPORTS_FIELD to ResourceType enum</li>
 *   <li>Register it in ResourceFactory</li>
 * </ol>
 * Zero changes were needed in booking-service, payment-service, or notification-service.
 * This proves the system is "open for extension, closed for modification".</p>
 */
@Getter
@Builder
public class SportsFieldSlot implements BookableResource {

    private final String resourceId;
    private final String fieldName;
    private final String sport;       // e.g., "Football", "Tennis", "Basketball"
    private final String surface;     // e.g., "Grass", "Artificial", "Clay"
    private final int maxPlayers;

    @Builder.Default
    private final Set<TimeSlot> bookedSlots = new HashSet<>();

    @Override
    public ResourceType getType() {
        return ResourceType.SPORTS_FIELD;
    }

    /**
     * Sports fields have a minimum booking duration of 1 hour.
     */
    @Override
    public Duration getMinBookingDuration() {
        return Duration.ofHours(1);
    }

    @Override
    public boolean isAvailableAt(TimeSlot slot) {
        return bookedSlots.stream().noneMatch(booked -> booked.overlapsWith(slot));
    }

    @Override
    public String getDisplayName() {
        return fieldName + " (" + sport + ", " + surface + ")";
    }
}
