package com.app.backend.auth.infrastructure.redis;

import com.app.backend.auth.domain.RefreshTokenRepository;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "auth:rt:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Long memberId, String hashedRefreshToken, Duration ttl) {
        try {
            validateRefreshTokenTtl(ttl);

            redisTemplate.opsForValue()
                    .set(key(memberId), hashedRefreshToken, ttl);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            throw new AuthException(AuthErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    @Override
    public Optional<String> findByMemberId(Long memberId) {
        try {
            return Optional.ofNullable(
                    redisTemplate.opsForValue().get(key(memberId))
            );
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            throw new AuthException(AuthErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        try {
            redisTemplate.delete(key(memberId));
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            throw new AuthException(AuthErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    @Override
    public boolean rotateIfMatches(
            Long memberId,
            String expectedRefreshTokenHash,
            String newRefreshTokenHash,
            Duration ttl
    ) {
        try {
            validateRefreshTokenTtl(ttl);

            String script = """
                    local current = redis.call('GET', KEYS[1])
                    if not current then
                        return 0
                    end
                    if current ~= ARGV[1] then
                        return 0
                    end
                    redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])
                    return 1
                    """;

            Long result = redisTemplate.execute(
                    new DefaultRedisScript<>(script, Long.class),
                    List.of(key(memberId)),
                    expectedRefreshTokenHash,
                    newRefreshTokenHash,
                    String.valueOf(ttl.toMillis())
            );

            return Long.valueOf(1L).equals(result);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            throw new AuthException(AuthErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    private void validateRefreshTokenTtl(Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new AuthException(AuthErrorCode.TOKEN_STORE_UNAVAILABLE);
        }
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
