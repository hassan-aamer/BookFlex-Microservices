package com.bookflex.resource.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * A doctor's appointment slot — concrete implementation of {@link BookableResource}.
 *
 * <p><b>Polymorphism (OOP Pillar)</b>: AppointmentSlot provides appointment-specific
 * behavior (shorter minimum duration, specialty info) while conforming to the same
 * BookableResource contract. The booking-service treats it identically to a Room.</p>
 *
 * <p><b>Liskov Substitution</b>: Appointments have a 30-minute minimum booking,
 * different from Room's 24-hour minimum. Both are valid BookableResource behaviors —
 * the contract says "return the minimum duration", not "return 24 hours".</p>
 */
@Getter
@Builder
public class AppointmentSlot implements BookableResource {

    private final String resourceId;
    private final String doctorName;
    private final String specialty;
    private final int durationMinutes;
    private final String clinicLocation;

    @Builder.Default
    private final Set<TimeSlot> bookedSlots = new HashSet<>();

    @Override
    public ResourceType getType() {
        return ResourceType.APPOINTMENT;
    }

    /**
     * Appointments have a minimum booking duration of 30 minutes.
     */
    @Override
    public Duration getMinBookingDuration() {
        return Duration.ofMinutes(30);
    }

    @Override
    public boolean isAvailableAt(TimeSlot slot) {
        return bookedSlots.stream().noneMatch(booked -> booked.overlapsWith(slot));
    }

    @Override
    public String getDisplayName() {
        return "Dr. " + doctorName + " (" + specialty + ")";
    }
}
