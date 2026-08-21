package com.bookflex.booking.service;

import com.bookflex.booking.client.PaymentClient;
import com.bookflex.booking.client.ResourceClient;
import com.bookflex.booking.client.UserClient;
import com.bookflex.booking.dto.CreateBookingRequest;
import com.bookflex.booking.entity.BookingEntity;
import com.bookflex.booking.entity.BookingStatus;
import com.bookflex.booking.event.BookingEventPublisher;
import com.bookflex.booking.exception.ResourceAlreadyBookedException;
import com.bookflex.booking.policy.CancellationPolicyResolver;
import com.bookflex.booking.repository.BookingRepository;
import com.bookflex.common.dto.PaymentRequest;
import com.bookflex.common.dto.PaymentResponse;
import com.bookflex.common.dto.ResourceDto;
import com.bookflex.common.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Concurrency Test — verifies Double Booking Prevention using Pessimistic Locking.
 *
 * <p><b>Requirement 7 from Prompt</b>:
 * "اكتب اختبار (test) يحاكي طلبين متزامنين لنفس الـ slot، ويثبت إن واحد بس ينجح والتاني يرجعله ResourceAlreadyBookedException."</p>
 *
 * <p>This test spawns 2 parallel threads attempting to book the exact same resource
 * at the exact same time slot. Using database pessimistic locking (@Lock(LockModeType.PESSIMISTIC_WRITE)),
 * exactly 1 thread will succeed and the other will throw {@link ResourceAlreadyBookedException}.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class ConcurrentBookingTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private ResourceClient resourceClient;

    @MockBean
    private PaymentClient paymentClient;

    @MockBean
    private UserClient userClient;

    @MockBean
    private BookingEventPublisher eventPublisher;

    private String resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

        resourceId = UUID.randomUUID().toString();
        startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        endTime = startTime.plusHours(2);

        // Mock Feign calls
        ResourceDto mockResource = ResourceDto.builder()
                .id(resourceId)
                .name("Conference Room A")
                .resourceType("ROOM")
                .pricePerSlot(new BigDecimal("100.00"))
                .build();

        when(resourceClient.getResourceById(any())).thenReturn(mockResource);
        doNothing().when(resourceClient).reserveResource(any());

        UserDto mockUser = UserDto.builder()
                .id("user-123")
                .email("test@bookflex.com")
                .fullName("Concurrent Tester")
                .build();

        when(userClient.getUserById(any())).thenReturn(mockUser);

        PaymentResponse mockPayment = PaymentResponse.builder()
                .paymentId(UUID.randomUUID().toString())
                .status("SUCCESS")
                .transactionReference("TXN-TEST-123")
                .build();

        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(mockPayment);
    }

    @Test
    @DisplayName("Simulate 2 concurrent threads booking the exact same slot — exactly 1 succeeds, 1 gets ResourceAlreadyBookedException")
    void testConcurrentBookingSameSlot() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1); // Synchronize thread start

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final String customerId = "customer-" + i;
            futures.add(executorService.submit(() -> {
                try {
                    latch.await(); // Wait until all threads are ready

                    CreateBookingRequest request = CreateBookingRequest.builder()
                            .resourceId(resourceId)
                            .startTime(startTime)
                            .endTime(endTime)
                            .cancellationPolicy("REFUNDABLE")
                            .build();

                    bookingService.createBooking(request, customerId);
                    successCount.incrementAndGet();
                } catch (ResourceAlreadyBookedException e) {
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Unexpected exception: " + e.getMessage());
                }
            }));
        }

        latch.countDown(); // Release threads simultaneously
        executorService.shutdown();
        boolean finished = executorService.awaitTermination(10, TimeUnit.SECONDS);

        assertTrue(finished, "Threads should finish execution within 10 seconds");

        // Verify results: exactly 1 booking succeeded, exactly 1 failed with ResourceAlreadyBookedException
        assertEquals(1, successCount.get(), "Exactly 1 booking should succeed");
        assertEquals(1, conflictCount.get(), "Exactly 1 booking should fail with ResourceAlreadyBookedException");

        // Verify DB state: only 1 non-cancelled booking exists
        List<BookingEntity> bookings = bookingRepository.findByResourceId(resourceId);
        long activeBookings = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
                .count();

        assertEquals(1, activeBookings, "Database should contain exactly 1 active booking for the slot");
    }
}
