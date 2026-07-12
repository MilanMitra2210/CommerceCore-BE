package com.commercecore.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test verifying that the Spring application context loads successfully.
 *
 * <p>This test catches:
 * <ul>
 *     <li>Missing beans or misconfigured components</li>
 *     <li>Circular dependency injection</li>
 *     <li>Invalid configuration properties</li>
 *     <li>Flyway migration failures</li>
 * </ul>
 *
 * <p>Uses the "dev" profile to connect to the local PostgreSQL instance.
 * In a CI environment, this would use a test profile with an embedded database.
 */
@SpringBootTest
@ActiveProfiles("dev")
class CommerceCoreApplicationTests {

    @Test
    void contextLoads() {
        // If this test passes, the application context is healthy.
    }

}
