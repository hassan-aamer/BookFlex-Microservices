package com.bookflex.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Discovery Server — central registry for all BookFlex microservices.
 *
 * <p>Every business service registers itself here on startup. The API Gateway
 * uses this registry to dynamically route requests to the correct service
 * instance, enabling horizontal scaling without hardcoded URLs.</p>
 *
 * <p><b>Singleton Pattern (via Spring)</b>: This application context, and the
 * Eureka server bean within it, are singletons managed by the Spring IoC container.
 * Spring beans are singleton-scoped by default — meaning one instance per
 * application context — which is the idiomatic Java/Spring equivalent of the
 * GoF Singleton pattern without the drawbacks of static access.</p>
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
