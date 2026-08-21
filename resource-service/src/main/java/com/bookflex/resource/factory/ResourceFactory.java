package com.bookflex.resource.factory;

import com.bookflex.resource.domain.AppointmentSlot;
import com.bookflex.resource.domain.BookableResource;
import com.bookflex.resource.domain.ResourceType;
import com.bookflex.resource.domain.Room;
import com.bookflex.resource.domain.SportsFieldSlot;
import com.bookflex.resource.entity.ResourceEntity;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern — creates the correct {@link BookableResource} domain object
 * from a persisted {@link ResourceEntity}.
 *
 * <p><b>Why Factory Pattern here?</b>
 * The resource-service stores all resources in a single table ({@code ResourceEntity})
 * with a {@code type} discriminator and a flexible JSON {@code attributes} column.
 * When business logic needs to work with domain objects (checking availability,
 * validating minimum duration), this factory hydrates the correct type.</p>
 *
 * <p><b>Open/Closed Principle integration</b>: To add a new resource type:
 * <ol>
 *   <li>Create a new class implementing {@code BookableResource}</li>
 *   <li>Add the type to {@code ResourceType} enum</li>
 *   <li>Add a case in this factory's switch expression</li>
 * </ol>
 * No other service code changes. The booking-service continues to work with
 * the {@code BookableResource} interface unchanged.</p>
 */
@Component
public class ResourceFactory {

    /**
     * Creates a domain-level BookableResource from a persisted entity.
     *
     * @param entity the JPA entity from the database
     * @return a fully-hydrated domain object of the correct concrete type
     * @throws IllegalArgumentException if the resource type is unknown
     */
    public BookableResource createFromEntity(ResourceEntity entity) {
        return switch (entity.getResourceType()) {
            case ROOM -> createRoom(entity);
            case APPOINTMENT -> createAppointment(entity);
            case SPORTS_FIELD -> createSportsField(entity);
        };
    }

    private Room createRoom(ResourceEntity entity) {
        return Room.builder()
                .resourceId(entity.getId().toString())
                .roomNumber(entity.getAttributeOrDefault("roomNumber", "N/A"))
                .capacity(entity.getIntAttribute("capacity", 2))
                .floor(entity.getIntAttribute("floor", 1))
                .amenities(entity.getListAttribute("amenities"))
                .description(entity.getDescription())
                .build();
    }

    private AppointmentSlot createAppointment(ResourceEntity entity) {
        return AppointmentSlot.builder()
                .resourceId(entity.getId().toString())
                .doctorName(entity.getAttributeOrDefault("doctorName", "Unknown"))
                .specialty(entity.getAttributeOrDefault("specialty", "General"))
                .durationMinutes(entity.getIntAttribute("durationMinutes", 30))
                .clinicLocation(entity.getAttributeOrDefault("clinicLocation", ""))
                .build();
    }

    private SportsFieldSlot createSportsField(ResourceEntity entity) {
        return SportsFieldSlot.builder()
                .resourceId(entity.getId().toString())
                .fieldName(entity.getAttributeOrDefault("fieldName", "Field"))
                .sport(entity.getAttributeOrDefault("sport", "General"))
                .surface(entity.getAttributeOrDefault("surface", "Grass"))
                .maxPlayers(entity.getIntAttribute("maxPlayers", 22))
                .build();
    }
}
