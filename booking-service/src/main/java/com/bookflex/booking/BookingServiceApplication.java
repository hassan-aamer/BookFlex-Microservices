package com.bookflex.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Booking Service — the core domain service of the BookFlex system.
 *
 * <p>This is where the majority of design patterns are applied:
 * State, Strategy, Builder, Template Method, Observer (via events), and the Saga orchestrator.</p>
 *
 * <p>{@code @EnableAsync} activates asynchronous method execution for non-critical operations
 * (notifications, stats updates) to avoid blocking the main booking flow.</p>
 *
 * <p>{@code @EnableFeignClients} enables declarative REST clients for inter-service communication
 * with resource-service, payment-service, and user-service.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
