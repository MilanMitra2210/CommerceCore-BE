package com.commercecore.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 *
 * <p><b>Foundation phase:</b> All endpoints are permitted. This is intentional —
 * security will be locked down when the Auth module is built.
 *
 * <p><b>Key decisions:</b>
 * <ul>
 *     <li>CSRF disabled — this is a stateless REST API using JWT, not server-rendered pages with cookies</li>
 *     <li>Session management set to STATELESS — no HTTP session is created or used</li>
 *     <li>CORS delegated to {@link CorsConfig} via {@code Customizer.withDefaults()}</li>
 * </ul>
 *
 * <p>When we build the Auth module, this class will be replaced with JWT filter chain,
 * role-based access control, and endpoint-level authorization rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll());

        return http.build();
    }

}
