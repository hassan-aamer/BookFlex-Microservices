package com.bookflex.user.security;

import com.bookflex.user.entity.User;
import com.bookflex.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads user-specific data for Spring Security authentication.
 *
 * <p><b>SRP</b>: This service has exactly one responsibility — bridge the gap between
 * our {@link UserRepository} and Spring Security's authentication mechanism.
 * It does not contain business logic (that's in {@link com.bookflex.user.service.UserService}).</p>
 *
 * <p><b>DIP (Dependency Inversion)</b>: Spring Security depends on the {@link UserDetailsService}
 * interface (abstraction), not on this concrete implementation. We can swap this
 * implementation (e.g., for LDAP or OAuth) without changing the security configuration.</p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        return UserPrincipal.fromUser(user);
    }
}
