package com.app.backend.auth.infrastructure.persistence;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaSocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderUserId(
            OAuthProvider provider,
            String providerUserId
    );

    boolean existsByMemberIdAndProvider(
            Long memberId,
            OAuthProvider provider
    );
}
