package com.commercecore.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * <p>Allows the two frontend applications to call the backend API:
 * <ul>
 *     <li>CommerceCore-FE (Next.js) on port 3002</li>
 *     <li>CommerceCore-Admin (React/Vite) on port 5173</li>
 * </ul>
 *
 * <p>Allowed origins are configurable per profile via {@code app.cors.allowed-origins}
 * in the application YAML files.
 *
 * <p><b>Why not allow all origins?</b> Even in development, restricting origins is good practice.
 * It prevents unauthorized websites from making API calls with a user's credentials.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(allowedOrigins.split(","))
        );
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "X-Requested-With", "Accept")
        );
        configuration.setExposedHeaders(
                List.of("Authorization")
        );
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
