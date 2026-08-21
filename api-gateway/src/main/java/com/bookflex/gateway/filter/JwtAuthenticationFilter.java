package com.bookflex.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Global gateway filter that validates JWT tokens on all protected routes.
 *
 * <p><b>Why a GlobalFilter and not per-route?</b>
 * Most endpoints require authentication. It's simpler and safer to validate globally
 * and whitelist public paths (login, register, Eureka, Swagger) than to protect each route individually.
 * This follows the "secure by default" principle.</p>
 *
 * <p>On successful validation, the filter extracts the user's ID and role from the JWT claims
 * and forwards them as {@code X-User-Id} and {@code X-User-Role} headers to downstream services.
 * This avoids each service needing its own JWT parsing logic.</p>
 *
 * <p><b>Spring Core — Constructor Injection</b>:
 * We use constructor injection (via {@code @Value}) instead of field injection.
 * Constructor injection guarantees that all required dependencies are available at construction time,
 * making the object fully initialized and testable without reflection.</p>
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

    /**
     * Paths that do NOT require JWT authentication.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/eureka",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    private final SecretKey signingKey;

    /**
     * <b>Constructor Injection</b>: The JWT secret is injected at construction time.
     * This is preferred over {@code @Autowired} field injection because:
     * <ul>
     *   <li>Dependencies are explicit and visible in the constructor signature</li>
     *   <li>The object is fully initialized after construction (no partial state)</li>
     *   <li>Easy to unit-test by passing values directly</li>
     * </ul>
     */
    public JwtAuthenticationFilter(@Value("${jwt.secret}") String jwtSecret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

        boolean isPublic = isPublicRequest(request);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            if (isPublic) {
                return chain.filter(exchange);
            }
            log.warn("Missing or malformed Authorization header for path: {}", path);
            return unauthorizedResponse(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);

            // Forward authenticated user info as headers to downstream services
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId)
                    .header(USER_ROLE_HEADER, role)
                    .header(USER_EMAIL_HEADER, email)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (JwtException e) {
            log.warn("JWT validation failed for path {}: {}", path, e.getMessage());
            if (isPublic) {
                return chain.filter(exchange);
            }
            return unauthorizedResponse(exchange);
        }
    }

    @Override
    public int getOrder() {
        // Run early in the filter chain — before routing
        return -1;
    }

    private boolean isPublicRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        org.springframework.http.HttpMethod method = request.getMethod();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }

        // Public GET endpoints for browsing resources & reading reviews
        if (org.springframework.http.HttpMethod.GET.equals(method) &&
                (path.startsWith("/api/resources") || path.startsWith("/api/reviews/resource"))) {
            return true;
        }

        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
