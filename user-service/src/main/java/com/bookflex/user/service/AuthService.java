package com.bookflex.user.service;

import com.bookflex.user.dto.AuthResponse;
import com.bookflex.user.dto.LoginRequest;
import com.bookflex.user.dto.RegisterRequest;
import com.bookflex.user.entity.User;
import com.bookflex.user.exception.EmailAlreadyExistsException;
import com.bookflex.user.exception.InvalidCredentialsException;
import com.bookflex.user.mapper.UserMapper;
import com.bookflex.user.repository.UserRepository;
import com.bookflex.user.security.JwtTokenProvider;
import com.bookflex.user.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user registration and authentication.
 *
 * <p><b>SRP</b>: This service handles only auth-related operations (register, login).
 * User profile queries belong to {@link UserService}.</p>
 *
 * <p><b>DIP</b>: Depends on {@link PasswordEncoder} interface (not BCryptPasswordEncoder directly)
 * and {@link AuthenticationManager} interface (not a concrete provider). Implementations are
 * injected via Spring's DI container.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    /**
     * Registers a new user.
     *
     * @param request validated registration data
     * @return authentication response with JWT token
     * @throws EmailAlreadyExistsException if the email is already taken
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} with role {}", savedUser.getEmail(), savedUser.getRole());

        // Authenticate the newly registered user and return a JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(savedUser.getId().toString())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole().name())
                .build();
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param request login credentials
     * @return authentication response with JWT token
     * @throws InvalidCredentialsException if the credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            String token = jwtTokenProvider.generateToken(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            log.info("User logged in: {}", request.getEmail());

            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .userId(userPrincipal.getId().toString())
                    .email(userPrincipal.getEmail())
                    .fullName(userPrincipal.getFullName())
                    .role(userPrincipal.getAuthorities().iterator().next()
                            .getAuthority().replace("ROLE_", ""))
                    .build();

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }
}
