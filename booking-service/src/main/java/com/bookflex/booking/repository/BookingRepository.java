package com.bookflex.booking.repository;

import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.entity.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link BookingEntity}.
 *
 * <p><b>Concurrency — Pessimistic Locking</b>:
 * The {@link #findConflictingBookingForUpdate} method uses {@code PESSIMISTIC_WRITE}
 * to prevent double booking. When a transaction locks a row, any other transaction
 * trying to lock the same row will block until the first one commits or rolls back.
 * This guarantees that only one booking can succeed for a given resource + time slot.</p>
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    /**
     * Pessimistic-lock query to detect conflicting bookings.
     *
     * <p><b>Why PESSIMISTIC_WRITE?</b>
     * Without locking, two concurrent requests could both read "no conflict" and both create bookings.
     * With PESSIMISTIC_WRITE, the first transaction locks matching rows, and the second transaction
     * waits until the first commits. If the first created a booking, the second will then see it
     * and be rejected. This is the database-level solution to the double-booking problem.</p>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b FROM BookingEntity b
            WHERE b.resourceId = :resourceId
              AND b.status NOT IN (com.bookflex.booking.entity.BookingStatus.CANCELLED)
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    Optional<BookingEntity> findConflictingBookingForUpdate(
            @Param("resourceId") String resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<BookingEntity> findByCustomerId(String customerId);

    List<BookingEntity> findByResourceId(String resourceId);

    List<BookingEntity> findByResourceIdAndStatus(String resourceId, BookingStatus status);

    @Query("""
            SELECT b FROM BookingEntity b
            WHERE b.resourceId = :resourceId
              AND b.status NOT IN (com.bookflex.booking.entity.BookingStatus.CANCELLED)
              AND b.startTime >= :dayStart
              AND b.startTime < :dayEnd
            ORDER BY b.startTime ASC
            """)
    List<BookingEntity> findByResourceAndDay(
            @Param("resourceId") String resourceId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    List<BookingEntity> findByStatus(BookingStatus status);
}
