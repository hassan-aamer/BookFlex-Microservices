package com.bookflex.review.repository;

import com.bookflex.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for review management and rating aggregations.
 */
@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    List<ReviewEntity> findByResourceIdOrderByCreatedAtDesc(UUID resourceId);

    Optional<ReviewEntity> findByBookingId(UUID bookingId);

    boolean existsByBookingId(UUID bookingId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.resourceId = :resourceId")
    Double calculateAverageRatingForResource(@Param("resourceId") UUID resourceId);

    long countByResourceId(UUID resourceId);
}
