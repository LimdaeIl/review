package com.app.backend.auth.infrastructure.persistence;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.domain.SocialAccount;
import com.app.backend.auth.domain.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SocialAccountRepositoryImpl implements SocialAccountRepository {

    private final JpaSocialAccountRepository repository;

    @Override
    public SocialAccount save(SocialAccount socialAccount) {
        return repository.save(socialAccount);
    }

    @Override
    public Optional<SocialAccount> findByProviderAndProviderUserId(
            OAuthProvider provider,
            String providerUserId
    ) {
        return repository.findByProviderAndProviderUserId(provider, providerUserId);
    }

    @Override
    public boolean existsByMemberIdAndProvider(Long memberId, OAuthProvider provider) {
        return repository.existsByMemberIdAndProvider(memberId, provider);
    }
}
