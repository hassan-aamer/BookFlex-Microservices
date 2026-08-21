package com.bookflex.user.service;

import com.bookflex.common.dto.UserDto;
import com.bookflex.user.dto.UserProfileResponse;
import com.bookflex.user.entity.User;
import com.bookflex.user.exception.UserNotFoundException;
import com.bookflex.user.mapper.UserMapper;
import com.bookflex.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for user profile queries and management.
 *
 * <p><b>SRP</b>: This service handles user profile operations.
 * Authentication logic is in {@link AuthService}.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Retrieves a user profile by ID.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(String userId) {
        User user = findUserById(userId);
        return userMapper.toProfileResponse(user);
    }

    /**
     * Returns a shared DTO for inter-service communication (via Feign).
     */
    @Transactional(readOnly = true)
    public UserDto getUserDtoById(String userId) {
        User user = findUserById(userId);
        return userMapper.toSharedDto(user);
    }

    /**
     * Lists all users (admin-only operation).
     * Uses Stream API for mapping.
     */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toProfileResponse)
                .toList();
    }

    private User findUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
