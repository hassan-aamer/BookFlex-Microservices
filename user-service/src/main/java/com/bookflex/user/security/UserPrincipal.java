package com.bookflex.user.security;

import com.bookflex.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Adapter between our {@link User} entity and Spring Security's {@link UserDetails}.
 *
 * <p><b>Adapter Pattern (structural)</b>: Spring Security expects a {@code UserDetails}
 * implementation, but our domain model uses {@code User}. This class adapts the
 * domain entity to the framework's expected interface without polluting the entity
 * with Spring Security concerns.</p>
 *
 * <p><b>SRP</b>: The {@code User} entity remains a pure JPA entity.
 * Security-specific behavior is encapsulated here.</p>
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String fullName;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Factory method to create a {@code UserPrincipal} from a domain {@code User}.
     */
    public static UserPrincipal fromUser(User user) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.isEnabled(),
                authorities
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
