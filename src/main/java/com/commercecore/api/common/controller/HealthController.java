package com.commercecore.api.common.controller;

import com.commercecore.api.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Health check endpoint to verify the application is running.
 *
 * <p>Used by:
 * <ul>
 *     <li>Load balancers (health probe)</li>
 *     <li>Docker health checks</li>
 *     <li>CI/CD pipelines (deployment verification)</li>
 *     <li>Frontend connectivity tests</li>
 * </ul>
 *
 * <p>This is a custom endpoint returning our standard {@link ApiResponse} format.
 * Spring Actuator's {@code /actuator/health} is also available for infrastructure monitoring.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Application health check")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check application health", description = "Returns the health status of the application")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = Map.of(
                "status", "UP",
                "timestamp", Instant.now(),
                "version", "1.0.0"
        );

        return ResponseEntity.ok(
                ApiResponse.success("Service is healthy", healthData)
        );
    }

}
