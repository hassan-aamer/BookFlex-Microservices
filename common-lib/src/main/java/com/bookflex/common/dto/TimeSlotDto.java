package com.bookflex.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a time window for a booking.
 * Immutable value object shared across services.
 *
 * <p><b>Value Object pattern</b>: Two TimeSlotDto instances with the same start/end
 * are considered semantically equal. Validated to ensure start is before end.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDto {

    @NotNull(message = "Start time must not be null")
    private LocalDateTime startTime;

    @NotNull(message = "End time must not be null")
    private LocalDateTime endTime;

    /**
     * Checks whether this time slot overlaps with another.
     */
    public boolean overlapsWith(TimeSlotDto other) {
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }
}
