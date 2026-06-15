package com.app.backend.auth.infrastructure.redis;

import com.app.backend.auth.domain.RefreshTokenRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "auth:refresh-token:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Long memberId, String hashedRefreshToken, Duration ttl) {
        redisTemplate.opsForValue()
                .set(key(memberId), hashedRefreshToken, ttl);
    }

    @Override
    public Optional<String> findByMemberId(Long memberId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(key(memberId))
        );
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        redisTemplate.delete(key(memberId));
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
