package com.bookflex.user.entity;

/**
 * User roles within the BookFlex system.
 *
 * <p><b>Role-Based Access Control (RBAC)</b>:
 * <ul>
 *   <li>{@code CUSTOMER} — can create/cancel their own bookings</li>
 *   <li>{@code PROVIDER} — can create/manage their own bookable resources</li>
 *   <li>{@code ADMIN} — full access to all operations and data</li>
 * </ul>
 * </p>
 */
public enum Role {
    CUSTOMER,
    PROVIDER,
    ADMIN
}
