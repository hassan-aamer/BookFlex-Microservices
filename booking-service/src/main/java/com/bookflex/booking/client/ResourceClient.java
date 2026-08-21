package com.bookflex.booking.client;

import com.bookflex.common.dto.ResourceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for communicating with resource-service.
 *
 * <p><b>DIP</b>: The booking-service depends on this interface (abstraction),
 * not on the concrete resource-service implementation. Spring Cloud creates
 * a proxy at runtime that handles HTTP calls, load balancing, and service discovery.</p>
 */
@FeignClient(name = "resource-service")
public interface ResourceClient {

    @GetMapping("/api/resources/{id}/dto")
    ResourceDto getResourceById(@PathVariable("id") String resourceId);

    @PutMapping("/api/resources/{id}/reserve")
    void reserveResource(@PathVariable("id") String resourceId);

    @PutMapping("/api/resources/{id}/release")
    void releaseResource(@PathVariable("id") String resourceId);
}
