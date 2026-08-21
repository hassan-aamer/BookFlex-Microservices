package com.bookflex.booking.service;

import com.bookflex.booking.client.PaymentClient;
import com.bookflex.booking.client.ResourceClient;
import com.bookflex.booking.client.UserClient;
import com.bookflex.booking.dto.BookingResponse;
import com.bookflex.booking.dto.CancelBookingRequest;
import com.bookflex.booking.dto.CreateBookingRequest;
import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.entity.BookingStatus;
import com.bookflex.booking.event.BookingEventPublisher;
import com.bookflex.booking.exception.BookingNotFoundException;
import com.bookflex.booking.exception.PaymentFailedException;
import com.bookflex.booking.exception.ResourceAlreadyBookedException;
import com.bookflex.booking.mapper.BookingMapper;
import com.bookflex.booking.policy.CancellationPolicy;
import com.bookflex.booking.policy.CancellationPolicyResolver;
import com.bookflex.booking.repository.BookingRepository;
import com.bookflex.booking.state.BookingState;
import com.bookflex.booking.state.BookingStateResolver;
import com.bookflex.common.dto.PaymentRequest;
import com.bookflex.common.dto.PaymentResponse;
import com.bookflex.common.dto.ResourceDto;
import com.bookflex.common.dto.UserDto;
import com.bookflex.common.event.BookingCancelledEvent;
import com.bookflex.common.event.BookingConfirmedEvent;
import com.bookflex.common.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core booking service — orchestrates the booking lifecycle.
 *
 * <p>This class is the centerpiece of the system, integrating:
 * <ul>
 *   <li><b>State Pattern</b>: State transitions via BookingStateResolver</li>
 *   <li><b>Strategy Pattern</b>: Cancellation policies via CancellationPolicyResolver</li>
 *   <li><b>Observer Pattern</b>: Event publishing via BookingEventPublisher</li>
 *   <li><b>Saga Pattern</b>: Orchestrated cross-service booking flow</li>
 *   <li><b>Pessimistic Locking</b>: Double-booking prevention via BookingRepository</li>
 *   <li><b>Collections Framework</b>: Map grouping, Stream API, custom Comparator</li>
 * </ul>
 * </p>
 *
 * <p><b>Template Method influence</b>: The createBooking flow follows a fixed sequence
 * (validate → check availability → lock → reserve → create → pay → confirm/compensate)
 * where some steps are customizable per resource type.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingEventPublisher eventPublisher;
    private final CancellationPolicyResolver policyResolver;

    // Feign clients for inter-service communication
    private final ResourceClient resourceClient;
    private final PaymentClient paymentClient;
    private final UserClient userClient;

    /**
     * Creates a booking using the Saga pattern (Orchestration-based).
     *
     * <p><b>Saga Pattern (Orchestration)</b>: The booking-service acts as the orchestrator,
     * calling each service in sequence and handling compensations on failure:
     * <ol>
     *   <li>Fetch resource info from resource-service (Feign)</li>
     *   <li>Check for conflicts using PESSIMISTIC_WRITE lock (Concurrency)</li>
     *   <li>Reserve the resource in resource-service (Saga step 1)</li>
     *   <li>Create the booking entity (PENDING state)</li>
     *   <li>Process payment via payment-service (Saga step 2)</li>
     *   <li>On success: Confirm booking, publish event</li>
     *   <li>On failure: Cancel booking, release resource (Compensating Transaction)</li>
     * </ol>
     * </p>
     *
     * <p><b>@Transactional</b>: Wraps the local database operations in a single transaction.
     * The Saga handles consistency ACROSS services — @Transactional handles consistency
     * WITHIN this service's database. This is explicitly NOT a distributed transaction.</p>
     */
    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;
    private final Map<String, java.util.concurrent.locks.ReentrantLock> resourceLocks = new java.util.concurrent.ConcurrentHashMap<>();

    public BookingResponse createBooking(CreateBookingRequest request, String customerId) {
        java.util.concurrent.locks.ReentrantLock lock = resourceLocks.computeIfAbsent(
                request.getResourceId(), k -> new java.util.concurrent.locks.ReentrantLock(true));
        lock.lock();
        try {
            return transactionTemplate.execute(status -> createBookingInternal(request, customerId));
        } finally {
            lock.unlock();
        }
    }

    private BookingResponse createBookingInternal(CreateBookingRequest request, String customerId) {
        // 1. Fetch resource info (Feign call to resource-service)
        ResourceDto resource;
        try {
            resource = resourceClient.getResourceById(request.getResourceId());
        } catch (Exception e) {
            log.error("Failed to fetch resource {}: {}", request.getResourceId(), e.getMessage());
            throw new com.bookflex.booking.exception.BookingNotFoundException(
                    "Resource not found: " + request.getResourceId());
        }

        // 2. Check for conflicting bookings using PESSIMISTIC_WRITE lock
        //    This is the critical section for double-booking prevention.
        bookingRepository.findConflictingBookingForUpdate(
                request.getResourceId(),
                request.getStartTime(),
                request.getEndTime()
        ).ifPresent(conflict -> {
            throw new ResourceAlreadyBookedException(
                    request.getResourceId(),
                    request.getStartTime() + " to " + request.getEndTime());
        });

        // 3. Reserve resource (Saga step 1)
        try {
            resourceClient.reserveResource(request.getResourceId());
        } catch (Exception e) {
            log.error("Failed to reserve resource {}: {}", request.getResourceId(), e.getMessage());
            throw new ResourceAlreadyBookedException(request.getResourceId(), "Resource unavailable");
        }

        // 4. Fetch customer info
        UserDto customer;
        try {
            customer = userClient.getUserById(customerId);
        } catch (Exception e) {
            log.warn("Failed to fetch user info for {}, using defaults", customerId);
            customer = UserDto.builder().id(customerId).email("").fullName("Unknown").build();
        }

        // 5. Build the booking entity (Builder Pattern)
        String policyName = request.getCancellationPolicy() != null
                ? request.getCancellationPolicy() : "REFUNDABLE";

        BookingEntity booking = BookingEntity.builder()
                .resourceId(request.getResourceId())
                .resourceName(resource.getName())
                .resourceType(resource.getResourceType())
                .customerId(customerId)
                .customerEmail(customer.getEmail())
                .customerName(customer.getFullName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalAmount(resource.getPricePerSlot())
                .status(BookingStatus.PENDING)
                .cancellationPolicy(policyName)
                .notes(request.getNotes())
                .build();

        BookingEntity savedBooking = bookingRepository.saveAndFlush(booking);
        log.info("Booking created: {} (PENDING)", savedBooking.getId());
        return processPaymentAndSaga(savedBooking, resource, customer, customerId);
    }


    private BookingResponse processPaymentAndSaga(BookingEntity savedBooking, ResourceDto resource, UserDto customer, String customerId) {


        // 6. Process payment (Saga step 2)
        try {
            PaymentResponse paymentResponse = paymentClient.processPayment(
                    PaymentRequest.builder()
                            .bookingId(savedBooking.getId().toString())
                            .customerId(customerId)
                            .amount(resource.getPricePerSlot())
                            .paymentMethod("CARD")
                            .description("Booking " + savedBooking.getId() + " for " + resource.getName())
                            .build());

            if ("SUCCESS".equals(paymentResponse.getStatus())) {
                // 7a. Payment succeeded — confirm booking (State Pattern transition)
                BookingState state = BookingStateResolver.resolve(savedBooking.getStatus());
                state.confirm(savedBooking);
                savedBooking.setPaymentId(paymentResponse.getPaymentId());
                savedBooking.setConfirmedAt(LocalDateTime.now());
                bookingRepository.save(savedBooking);

                // Publish confirmation event (Observer Pattern)
                eventPublisher.publishBookingConfirmed(BookingConfirmedEvent.builder()
                        .bookingId(savedBooking.getId().toString())
                        .resourceId(savedBooking.getResourceId())
                        .resourceName(savedBooking.getResourceName())
                        .customerId(customerId)
                        .customerEmail(customer.getEmail())
                        .customerName(customer.getFullName())
                        .startTime(savedBooking.getStartTime())
                        .endTime(savedBooking.getEndTime())
                        .totalAmount(savedBooking.getTotalAmount())
                        .confirmedAt(savedBooking.getConfirmedAt())
                        .build());

                log.info("Booking {} CONFIRMED with payment {}", savedBooking.getId(), paymentResponse.getPaymentId());
            } else {
                // 7b. Payment failed — compensate
                compensateBooking(savedBooking, "Payment returned status: " + paymentResponse.getStatus());
            }

        } catch (Exception e) {
            // 7b. Payment service error — compensate (Saga compensating transaction)
            log.error("Payment failed for booking {}: {}", savedBooking.getId(), e.getMessage());
            compensateBooking(savedBooking, e.getMessage());
        }

        return bookingMapper.toResponse(savedBooking);
    }

    /**
     * Cancels a booking with refund calculation using the Strategy Pattern.
     */
    @Transactional
    public BookingResponse cancelBooking(String bookingId, CancelBookingRequest request, String customerId) {
        BookingEntity booking = findBookingById(bookingId);

        // State Pattern — validate transition
        BookingState state = BookingStateResolver.resolve(booking.getStatus());
        state.cancel(booking);

        booking.setCancellationReason(request.getReason());
        booking.setCancelledAt(LocalDateTime.now());

        // Strategy Pattern — calculate refund based on the booking's policy
        CancellationPolicy policy = policyResolver.resolve(booking.getCancellationPolicy());
        BigDecimal refundAmount = policy.calculateRefund(
                booking.getTotalAmount(), booking.getStartTime(), LocalDateTime.now());

        bookingRepository.save(booking);

        // Release resource (Saga compensation)
        try {
            resourceClient.releaseResource(booking.getResourceId());
        } catch (Exception e) {
            log.error("Failed to release resource {} during cancellation: {}",
                    booking.getResourceId(), e.getMessage());
        }

        // Process refund if applicable
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                paymentClient.processRefund(PaymentRequest.builder()
                        .bookingId(bookingId)
                        .customerId(customerId)
                        .amount(refundAmount)
                        .description("Refund for booking " + bookingId)
                        .build());
            } catch (Exception e) {
                log.error("Refund failed for booking {}: {}", bookingId, e.getMessage());
            }
        }

        // Publish cancellation event (Observer Pattern)
        eventPublisher.publishBookingCancelled(BookingCancelledEvent.builder()
                .bookingId(bookingId)
                .resourceId(booking.getResourceId())
                .resourceName(booking.getResourceName())
                .customerId(customerId)
                .customerEmail(booking.getCustomerEmail())
                .customerName(booking.getCustomerName())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .refundAmount(refundAmount)
                .cancellationReason(request.getReason())
                .cancelledAt(booking.getCancelledAt())
                .build());

        log.info("Booking {} CANCELLED. Refund: {}", bookingId, refundAmount);
        return bookingMapper.toResponse(booking);
    }

    /**
     * Completes a confirmed booking.
     */
    @Transactional
    public BookingResponse completeBooking(String bookingId) {
        BookingEntity booking = findBookingById(bookingId);
        BookingState state = BookingStateResolver.resolve(booking.getStatus());
        state.complete(booking);
        booking.setCompletedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        log.info("Booking {} COMPLETED", bookingId);
        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String bookingId) {
        return bookingMapper.toResponse(findBookingById(bookingId));
    }

    /**
     * Returns customer's bookings, sorted by start time (custom Comparator).
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(String customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .sorted(Comparator.comparing(BookingEntity::getStartTime).reversed())
                .map(bookingMapper::toResponse)
                .toList();
    }

    /**
     * Returns bookings for a resource, sorted by start time.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByResource(String resourceId) {
        return bookingRepository.findByResourceId(resourceId)
                .stream()
                .sorted(Comparator.comparing(BookingEntity::getStartTime))
                .map(bookingMapper::toResponse)
                .toList();
    }

    /**
     * Groups bookings by date — demonstrates Map<LocalDate, List<Booking>> from Collections Framework.
     *
     * <p><b>Collections Framework</b>: Uses Stream API's {@code Collectors.groupingBy}
     * to create a map of bookings grouped by their start date. This is more expressive
     * and less error-prone than manual iteration with a Map.</p>
     */
    @Transactional(readOnly = true)
    public Map<LocalDate, List<BookingResponse>> getBookingsByResourceGroupedByDate(String resourceId) {
        return bookingRepository.findByResourceId(resourceId)
                .stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .sorted(Comparator.comparing(BookingEntity::getStartTime))
                .map(bookingMapper::toResponse)
                .collect(Collectors.groupingBy(
                        booking -> booking.getStartTime().toLocalDate()));
    }

    /**
     * Saga compensating transaction — cancels the booking and releases the resource
     * when payment fails.
     */
    private void compensateBooking(BookingEntity booking, String reason) {
        log.warn("Compensating booking {}: {}", booking.getId(), reason);

        BookingState state = BookingStateResolver.resolve(booking.getStatus());
        state.cancel(booking);
        booking.setCancellationReason("Payment failed: " + reason);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Release the reserved resource
        try {
            resourceClient.releaseResource(booking.getResourceId());
        } catch (Exception e) {
            log.error("Failed to release resource during compensation: {}", e.getMessage());
        }
    }

    private BookingEntity findBookingById(String bookingId) {
        return bookingRepository.findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}
