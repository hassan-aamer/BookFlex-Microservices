package com.bookflex.user.controller;

import com.bookflex.common.dto.UserDto;
import com.bookflex.user.dto.UserProfileResponse;
import com.bookflex.user.security.UserPrincipal;
import com.bookflex.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user profile operations.
 *
 * <p><b>Spring Security — @PreAuthorize</b>: Fine-grained role-based access control.
 * - GET /api/users/me — any authenticated user
 * - GET /api/users/{id} — any authenticated user (self or admin)
 * - GET /api/users — ADMIN only
 * - GET /api/users/{id}/dto — internal (Feign) — any authenticated service</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse profile = userService.getUserById(principal.getId().toString());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String id) {
        UserProfileResponse profile = userService.getUserById(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users (Admin only)")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Internal endpoint used by other services via OpenFeign to resolve user info.
     * Returns the shared UserDto (not the full profile).
     */
    @GetMapping("/{id}/dto")
    @Operation(summary = "Get user DTO (internal)", description = "Used by other microservices via Feign")
    public ResponseEntity<UserDto> getUserDtoById(@PathVariable String id) {
        UserDto userDto = userService.getUserDtoById(id);
        return ResponseEntity.ok(userDto);
    }
}
