package com.bookflex.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway — the single entry point for all client requests.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Route requests to the correct microservice via Eureka-based service discovery</li>
 *   <li>Validate JWT tokens on protected endpoints (via {@code JwtAuthenticationFilter})</li>
 *   <li>Forward authenticated user info (userId, role) as headers to downstream services</li>
 *   <li>Apply cross-cutting concerns: CORS, rate limiting (future)</li>
 * </ul>
 * </p>
 *
 * <p><b>Singleton Pattern (via Spring)</b>: All Spring-managed beans in this context
 * are singleton-scoped by default — one instance per application context.
 * This is the idiomatic replacement for the GoF Singleton in Spring applications.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
