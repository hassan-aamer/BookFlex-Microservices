package com.bookflex.booking.policy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves the appropriate {@link CancellationPolicy} by name.
 *
 * <p><b>DIP (Dependency Inversion) + Strategy Pattern wiring</b>:
 * Spring auto-discovers all CancellationPolicy implementations and injects them
 * as a List. This resolver builds a lookup map by policy name, allowing runtime
 * selection based on the booking's configured policy.</p>
 *
 * <p>Adding a new policy requires only creating a new @Component class implementing
 * CancellationPolicy — no changes here or in the service layer (OCP).</p>
 */
@Component
public class CancellationPolicyResolver {

    private final Map<String, CancellationPolicy> policyMap;

    /**
     * Constructor Injection: Spring injects ALL beans implementing CancellationPolicy.
     * We build a name→policy map using Stream API for O(1) lookups.
     */
    public CancellationPolicyResolver(List<CancellationPolicy> policies) {
        this.policyMap = policies.stream()
                .collect(Collectors.toMap(
                        CancellationPolicy::getPolicyName,
                        Function.identity()
                ));
    }

    /**
     * Resolves the policy by name. Falls back to REFUNDABLE if the name is unknown.
     */
    public CancellationPolicy resolve(String policyName) {
        return policyMap.getOrDefault(policyName, policyMap.get("REFUNDABLE"));
    }
}
