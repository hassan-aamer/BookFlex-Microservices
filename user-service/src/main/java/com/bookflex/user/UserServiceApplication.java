package com.bookflex.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service — manages authentication, authorization, and user profiles.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>User registration with role assignment (CUSTOMER, PROVIDER, ADMIN)</li>
 *   <li>JWT-based login returning a signed access token</li>
 *   <li>User profile retrieval (own profile and admin listing)</li>
 *   <li>Serves as the identity authority — other services trust JWT tokens issued here</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
