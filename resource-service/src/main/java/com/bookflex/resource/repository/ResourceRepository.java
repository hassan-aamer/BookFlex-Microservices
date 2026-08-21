package com.bookflex.resource.repository;

import com.bookflex.resource.domain.ResourceType;
import com.bookflex.resource.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link ResourceEntity}.
 *
 * <p>Uses custom {@code @Query} annotations where Spring Data's query derivation
 * would be less readable or efficient.</p>
 */
@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, UUID> {

    List<ResourceEntity> findByResourceType(ResourceType resourceType);

    List<ResourceEntity> findByProviderId(String providerId);

    List<ResourceEntity> findByStatus(String status);

    @Query("SELECT r FROM ResourceEntity r WHERE r.resourceType = :type AND r.status = 'ACTIVE'")
    List<ResourceEntity> findActiveByType(@Param("type") ResourceType type);

    @Query("SELECT r FROM ResourceEntity r WHERE r.status = 'ACTIVE'")
    List<ResourceEntity> findAllActive();

    Optional<ResourceEntity> findByIdAndProviderId(UUID id, String providerId);
}
