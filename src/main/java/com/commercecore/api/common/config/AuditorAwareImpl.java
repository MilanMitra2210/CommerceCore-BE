package com.commercecore.api.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Provides the current auditor (user identity) for JPA auditing.
 *
 * <p><b>Foundation phase:</b> Returns "system" as the auditor since there is
 * no authentication yet.
 *
 * <p><b>After Auth module is built:</b> This implementation will be updated to
 * extract the authenticated user's ID from the Spring Security context:
 * <pre>{@code
 * SecurityContextHolder.getContext().getAuthentication().getName();
 * }</pre>
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        // TODO: Replace with SecurityContext lookup when Auth module is built
        // String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // return Optional.ofNullable(userId);
        return Optional.of("system");
    }

}
