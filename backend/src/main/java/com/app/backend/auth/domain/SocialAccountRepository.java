package com.app.backend.auth.domain;

import java.util.Optional;

public interface SocialAccountRepository {

    SocialAccount save(SocialAccount socialAccount);

    Optional<SocialAccount> findByProviderAndProviderUserId(OAuthProvider provider,
            String providerUserId);

    boolean existsByMemberIdAndProvider(
            Long memberId,
            OAuthProvider provider
    );
}
