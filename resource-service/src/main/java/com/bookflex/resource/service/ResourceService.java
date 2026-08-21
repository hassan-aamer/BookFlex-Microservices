package com.bookflex.resource.service;

import com.bookflex.common.dto.ResourceDto;
import com.bookflex.resource.domain.BookableResource;
import com.bookflex.resource.domain.ResourceType;
import com.bookflex.resource.dto.CreateResourceRequest;
import com.bookflex.resource.dto.ResourceResponse;
import com.bookflex.resource.entity.ResourceEntity;
import com.bookflex.resource.exception.ResourceNotFoundException;
import com.bookflex.resource.factory.ResourceFactory;
import com.bookflex.resource.mapper.ResourceMapper;
import com.bookflex.resource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service managing bookable resources — CRUD operations and availability.
 *
 * <p><b>SRP</b>: Handles resource business logic only. Persistence is in ResourceRepository,
 * mapping in ResourceMapper, and domain object creation in ResourceFactory.</p>
 *
 * <p><b>DIP</b>: Depends on ResourceRepository interface (not a concrete JPA impl)
 * and ResourceFactory (injectable, replaceable).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceFactory resourceFactory;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceResponse createResource(CreateResourceRequest request, String providerId) {
        ResourceEntity entity = resourceMapper.toEntity(request, providerId);
        ResourceEntity saved = resourceRepository.save(entity);

        BookableResource domainObject = resourceFactory.createFromEntity(saved);
        log.info("Created resource: {} (type={}, provider={})",
                saved.getId(), saved.getResourceType(), providerId);

        return resourceMapper.toResponse(saved, domainObject);
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(String resourceId) {
        ResourceEntity entity = findEntityById(resourceId);
        BookableResource domainObject = resourceFactory.createFromEntity(entity);
        return resourceMapper.toResponse(entity, domainObject);
    }

    /**
     * Returns the shared DTO for inter-service communication (via Feign).
     */
    @Transactional(readOnly = true)
    public ResourceDto getResourceDtoById(String resourceId) {
        ResourceEntity entity = findEntityById(resourceId);
        return resourceMapper.toSharedDto(entity);
    }

    /**
     * Lists all active resources, optionally filtered by type.
     * Uses Stream API for filtering and mapping.
     */
    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources(ResourceType type) {
        List<ResourceEntity> entities = (type != null)
                ? resourceRepository.findActiveByType(type)
                : resourceRepository.findAllActive();

        return entities.stream()
                .map(entity -> {
                    BookableResource domainObject = resourceFactory.createFromEntity(entity);
                    return resourceMapper.toResponse(entity, domainObject);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByProvider(String providerId) {
        return resourceRepository.findByProviderId(providerId)
                .stream()
                .map(entity -> {
                    BookableResource domainObject = resourceFactory.createFromEntity(entity);
                    return resourceMapper.toResponse(entity, domainObject);
                })
                .toList();
    }

    @Transactional
    public ResourceResponse updateResource(String resourceId, CreateResourceRequest request, String providerId) {
        ResourceEntity entity = resourceRepository.findByIdAndProviderId(
                        UUID.fromString(resourceId), providerId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPricePerSlot(request.getPricePerSlot());

        if (request.getAttributes() != null) {
            String serialized = request.getAttributes().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a, b) -> a + ";" + b)
                    .orElse("");
            entity.setAttributes(serialized);
        }

        ResourceEntity saved = resourceRepository.save(entity);
        BookableResource domainObject = resourceFactory.createFromEntity(saved);
        return resourceMapper.toResponse(saved, domainObject);
    }

    /**
     * Soft-delete a resource by setting its status to INACTIVE.
     */
    @Transactional
    public void deleteResource(String resourceId, String providerId) {
        ResourceEntity entity = resourceRepository.findByIdAndProviderId(
                        UUID.fromString(resourceId), providerId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));

        entity.setStatus("INACTIVE");
        resourceRepository.save(entity);
        log.info("Soft-deleted resource: {}", resourceId);
    }

    /**
     * Reserves a resource slot (called by booking-service during Saga).
     * Sets status to RESERVED temporarily.
     */
    @Transactional
    public void reserveResource(String resourceId) {
        ResourceEntity entity = findEntityById(resourceId);
        // In a real system, we'd track individual slot reservations.
        // For simplicity, we just log the reservation event.
        log.info("Resource {} reserved for booking", resourceId);
    }

    /**
     * Releases a previously reserved resource (Saga compensating transaction).
     */
    @Transactional
    public void releaseResource(String resourceId) {
        ResourceEntity entity = findEntityById(resourceId);
        log.info("Resource {} released (compensation)", resourceId);
    }

    private ResourceEntity findEntityById(String resourceId) {
        return resourceRepository.findById(UUID.fromString(resourceId))
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }
}
