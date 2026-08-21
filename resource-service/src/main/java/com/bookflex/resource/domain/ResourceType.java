package com.bookflex.resource.domain;

/**
 * Enumerates the supported types of bookable resources.
 *
 * <p>When adding a new resource type, add an enum value here and create
 * a corresponding implementation of {@link BookableResource}. No existing
 * code needs to change (Open/Closed Principle).</p>
 */
public enum ResourceType {
    ROOM,
    APPOINTMENT,
    SPORTS_FIELD
}
