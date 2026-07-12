package com.commercecore.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA Auditing to automatically populate {@code @CreatedDate},
 * {@code @LastModifiedDate}, {@code @CreatedBy}, and {@code @LastModifiedBy}
 * fields on entities extending {@link com.commercecore.api.common.entity.BaseEntity}.
 *
 * <p>The {@link AuditorAware} bean provides the current user's identity.
 * During the foundation phase, this returns "system". Once the Auth module
 * is built, it will read the authenticated user's ID from the Security context.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

}
