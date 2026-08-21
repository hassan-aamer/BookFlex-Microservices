package com.bookflex.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for resource details.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private String id;
    private String name;
    private String resourceType;
    private String description;
    private BigDecimal pricePerSlot;
    private String providerId;
    private String status;
    private Map<String, String> attributes;
    private String displayName;
    private String minBookingDuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
