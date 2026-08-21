package com.bookflex.resource.entity;

import com.bookflex.resource.domain.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity storing all bookable resources in a single table.
 *
 * <p><b>Design decision — single table with attributes map</b>:
 * Instead of using JPA inheritance (SINGLE_TABLE or JOINED), we use a flexible
 * {@code attributes} map stored as a delimited string. This allows adding new
 * resource types without schema changes, supporting the Open/Closed Principle
 * at the persistence layer.</p>
 *
 * <p>The {@link com.bookflex.resource.factory.ResourceFactory} hydrates these
 * flat attributes into the correct domain object (Room, AppointmentSlot, etc.).</p>
 */
@Entity
@Table(name = "resources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 30)
    private ResourceType resourceType;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price_per_slot", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSlot;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * Flexible key-value attributes stored as a delimited string.
     * Format: "key1=value1;key2=value2;key3=value3"
     * This avoids the need for a separate table or JSON column (H2 compatible).
     */
    @Column(name = "attributes", length = 2000)
    private String attributes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Attribute helpers ───────────────────────────────────────────────

    /**
     * Parses the attributes string into a Map for convenient access.
     */
    public Map<String, String> getAttributesMap() {
        if (attributes == null || attributes.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        for (String pair : attributes.split(";")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }

    public String getAttributeOrDefault(String key, String defaultValue) {
        return getAttributesMap().getOrDefault(key, defaultValue);
    }

    public int getIntAttribute(String key, int defaultValue) {
        String value = getAttributesMap().get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<String> getListAttribute(String key) {
        String value = getAttributesMap().get(key);
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }

    /**
     * Sets a single attribute in the attributes string.
     */
    public void setAttribute(String key, String value) {
        Map<String, String> map = new HashMap<>(getAttributesMap());
        map.put(key, value);
        this.attributes = map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + ";" + b)
                .orElse("");
    }
}
