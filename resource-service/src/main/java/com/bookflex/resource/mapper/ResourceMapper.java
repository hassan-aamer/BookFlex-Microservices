package com.bookflex.resource.mapper;

import com.bookflex.common.dto.ResourceDto;
import com.bookflex.resource.domain.BookableResource;
import com.bookflex.resource.dto.CreateResourceRequest;
import com.bookflex.resource.dto.ResourceResponse;
import com.bookflex.resource.entity.ResourceEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Maps between ResourceEntity, domain objects, and DTOs.
 */
@Component
public class ResourceMapper {

    public ResourceEntity toEntity(CreateResourceRequest request, String providerId) {
        ResourceEntity entity = ResourceEntity.builder()
                .name(request.getName())
                .resourceType(request.getResourceType())
                .description(request.getDescription())
                .pricePerSlot(request.getPricePerSlot())
                .providerId(providerId)
                .build();

        // Serialize type-specific attributes
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            String serialized = request.getAttributes().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a, b) -> a + ";" + b)
                    .orElse("");
            entity.setAttributes(serialized);
        }

        return entity;
    }

    public ResourceResponse toResponse(ResourceEntity entity, BookableResource domainObject) {
        return ResourceResponse.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .resourceType(entity.getResourceType().name())
                .description(entity.getDescription())
                .pricePerSlot(entity.getPricePerSlot())
                .providerId(entity.getProviderId())
                .status(entity.getStatus())
                .attributes(entity.getAttributesMap())
                .displayName(domainObject.getDisplayName())
                .minBookingDuration(domainObject.getMinBookingDuration().toString())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ResourceResponse toResponse(ResourceEntity entity) {
        return ResourceResponse.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .resourceType(entity.getResourceType().name())
                .description(entity.getDescription())
                .pricePerSlot(entity.getPricePerSlot())
                .providerId(entity.getProviderId())
                .status(entity.getStatus())
                .attributes(entity.getAttributesMap())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts to shared DTO for inter-service communication.
     */
    public ResourceDto toSharedDto(ResourceEntity entity) {
        return ResourceDto.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .resourceType(entity.getResourceType().name())
                .description(entity.getDescription())
                .pricePerSlot(entity.getPricePerSlot())
                .providerId(entity.getProviderId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
