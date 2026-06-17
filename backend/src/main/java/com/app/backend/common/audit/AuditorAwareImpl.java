package com.app.backend.common.audit;

import com.app.backend.auth.infrastructure.security.AuthMember;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<Long> {

    private static final Long SYSTEM = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthMember authMember) {
            return Optional.of(authMember.memberId());
        }

        return Optional.of(SYSTEM);

    }
}
