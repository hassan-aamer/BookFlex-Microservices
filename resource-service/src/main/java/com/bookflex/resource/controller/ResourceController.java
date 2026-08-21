package com.bookflex.resource.controller;

import com.bookflex.common.dto.ResourceDto;
import com.bookflex.resource.domain.ResourceType;
import com.bookflex.resource.dto.CreateResourceRequest;
import com.bookflex.resource.dto.ResourceResponse;
import com.bookflex.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bookable resource management.
 *
 * <p><b>Thin Controller (SRP)</b>: Only handles HTTP concerns — delegates all logic to ResourceService.</p>
 *
 * <p>Provider ID is extracted from the {@code X-User-Id} header set by the API Gateway
 * after JWT validation. This avoids re-parsing the JWT in each service.</p>
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Bookable resource management")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @Operation(summary = "Create a new resource (Provider only)")
    public ResponseEntity<ResourceResponse> createResource(
            @Valid @RequestBody CreateResourceRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String providerId) {

        // Fallback for direct access without gateway
        if (providerId == null) providerId = "system";

        ResourceResponse response = resourceService.createResource(request, providerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable String id) {
        ResourceResponse response = resourceService.getResourceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all active resources (optional type filter)")
    public ResponseEntity<List<ResourceResponse>> getAllResources(
            @RequestParam(required = false) ResourceType type) {
        List<ResourceResponse> resources = resourceService.getAllResources(type);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "List resources by provider")
    public ResponseEntity<List<ResourceResponse>> getResourcesByProvider(
            @PathVariable String providerId) {
        List<ResourceResponse> resources = resourceService.getResourcesByProvider(providerId);
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a resource (Provider only)")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable String id,
            @Valid @RequestBody CreateResourceRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String providerId) {

        if (providerId == null) providerId = "system";

        ResourceResponse response = resourceService.updateResource(id, request, providerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a resource (Provider only)")
    public ResponseEntity<Void> deleteResource(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String providerId) {

        if (providerId == null) providerId = "system";

        resourceService.deleteResource(id, providerId);
        return ResponseEntity.noContent().build();
    }

    // ── Internal endpoints (used by other services via Feign) ──────────

    @GetMapping("/{id}/dto")
    @Operation(summary = "Get resource DTO (internal)", description = "Used by booking-service via Feign")
    public ResponseEntity<ResourceDto> getResourceDtoById(@PathVariable String id) {
        ResourceDto dto = resourceService.getResourceDtoById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/reserve")
    @Operation(summary = "Reserve a resource (Saga step)")
    public ResponseEntity<Void> reserveResource(@PathVariable String id) {
        resourceService.reserveResource(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/release")
    @Operation(summary = "Release a resource (Saga compensation)")
    public ResponseEntity<Void> releaseResource(@PathVariable String id) {
        resourceService.releaseResource(id);
        return ResponseEntity.ok().build();
    }
}
