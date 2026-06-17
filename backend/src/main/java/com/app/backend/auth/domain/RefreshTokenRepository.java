package com.app.backend.auth.domain;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenRepository {

    void save(Long memberId, String hashedRefreshToken, Duration ttl);

    Optional<String> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    RefreshTokenRotationResult rotateIfMatches(
            Long memberId,
            String expectedRefreshTokenHash,
            String newRefreshTokenHash,
            Duration ttl
    );
}
