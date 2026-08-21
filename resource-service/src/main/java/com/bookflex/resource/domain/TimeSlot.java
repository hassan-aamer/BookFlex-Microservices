package com.bookflex.resource.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Value Object representing a time window for bookings.
 *
 * <p><b>Value Object (DDD)</b>: A TimeSlot is defined entirely by its start and end times.
 * Two TimeSlots with the same start/end are considered equal (hence {@code @EqualsAndHashCode}).
 * TimeSlots are used in {@code Set<TimeSlot>} to guarantee uniqueness of available slots.</p>
 *
 * <p><b>Encapsulation (OOP Pillar)</b>: Validation logic is encapsulated in the constructor —
 * it's impossible to create an invalid TimeSlot (end before start, null values).
 * The overlap-checking logic is also encapsulated here rather than scattered in callers.</p>
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class TimeSlot {

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates a validated TimeSlot.
     *
     * @throws IllegalArgumentException if start is null, end is null, or start is after end
     */
    public static TimeSlot of(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time must not be null");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Start time must be before end time. Got start=" + startTime + ", end=" + endTime);
        }
        return new TimeSlot(startTime, endTime);
    }

    /**
     * Checks whether this time slot overlaps with another.
     * Two slots overlap if one starts before the other ends and vice versa.
     */
    public boolean overlapsWith(TimeSlot other) {
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    /**
     * Returns the duration of this time slot.
     */
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    @Override
    public String toString() {
        return "[" + startTime + " → " + endTime + "]";
    }
}
