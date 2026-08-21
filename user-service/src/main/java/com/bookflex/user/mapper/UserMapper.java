package com.bookflex.user.mapper;

import com.bookflex.common.dto.UserDto;
import com.bookflex.user.dto.RegisterRequest;
import com.bookflex.user.dto.UserProfileResponse;
import com.bookflex.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * Maps between User entity, DTOs, and shared DTOs.
 *
 * <p><b>SRP</b>: Mapping logic is isolated from services and controllers,
 * preventing entity-to-DTO conversion code from being scattered everywhere.</p>
 */
@Component
public class UserMapper {

    /**
     * Converts a RegisterRequest DTO to a User entity.
     * Note: password must be encoded by the service layer before saving.
     */
    public User toEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword()) // Raw — service encodes before save
                .fullName(request.getFullName())
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    /**
     * Converts a User entity to a UserProfileResponse DTO (excludes password).
     */
    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Converts a User entity to the shared UserDto (used for inter-service communication).
     */
    public UserDto toSharedDto(User user) {
        return UserDto.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
