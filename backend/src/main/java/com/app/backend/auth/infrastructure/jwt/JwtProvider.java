package com.app.backend.auth.infrastructure.jwt;

import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String ROLE_CLAIM = "role";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private static final String ACCESS_TOKEN = "ACCESS";
    private static final String REFRESH_TOKEN = "REFRESH";

    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    public JwtProvider(JwtProperties jwtProperties, Clock clock) {
        this.jwtProperties = jwtProperties;
        this.clock = clock;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createAccessToken(Long memberId, String role) {
        return createToken(
                memberId,
                ACCESS_TOKEN,
                role,
                jwtProperties.accessTokenExpirationMillis()
        );
    }

    public String createRefreshToken(Long memberId) {
        return createToken(
                memberId,
                REFRESH_TOKEN,
                null,
                jwtProperties.refreshTokenExpirationMillis()
        );
    }

    public void validateAccessToken(String token) {
        Claims claims = parseClaims(
                token,
                AuthErrorCode.EXPIRED_ACCESS_TOKEN,
                AuthErrorCode.INVALID_ACCESS_TOKEN
        );

        validateTokenType(claims, ACCESS_TOKEN);
    }

    public void validateRefreshToken(String token) {
        Claims claims = parseClaims(
                token,
                AuthErrorCode.EXPIRED_REFRESH_TOKEN,
                AuthErrorCode.INVALID_REFRESH_TOKEN
        );

        validateTokenType(claims, REFRESH_TOKEN);
    }

    public Long getMemberIdFromAccessToken(String token) {
        Claims claims = parseClaims(
                token,
                AuthErrorCode.EXPIRED_ACCESS_TOKEN,
                AuthErrorCode.INVALID_ACCESS_TOKEN
        );

        validateTokenType(claims, ACCESS_TOKEN);

        return Long.valueOf(claims.getSubject());
    }

    public Long getMemberIdFromRefreshToken(String token) {
        Claims claims = parseClaims(
                token,
                AuthErrorCode.EXPIRED_REFRESH_TOKEN,
                AuthErrorCode.INVALID_REFRESH_TOKEN
        );

        validateTokenType(claims, REFRESH_TOKEN);

        return Long.valueOf(claims.getSubject());
    }

    public String getRoleFromAccessToken(String token) {
        Claims claims = parseClaims(
                token,
                AuthErrorCode.EXPIRED_ACCESS_TOKEN,
                AuthErrorCode.INVALID_ACCESS_TOKEN
        );

        validateTokenType(claims, ACCESS_TOKEN);

        return claims.get(ROLE_CLAIM, String.class);
    }

    public long getAccessTokenExpirationMillis() {
        return jwtProperties.accessTokenExpirationMillis();
    }

    public long getRefreshTokenExpirationMillis() {
        return jwtProperties.refreshTokenExpirationMillis();
    }

    public long getRefreshTokenExpirationSeconds() {
        return jwtProperties.refreshTokenExpirationMillis() / 1000;
    }

    private String createToken(
            Long memberId,
            String tokenType,
            String role,
            long expirationMillis
    ) {
        Date now = now();
        Date expiration = new Date(now.getTime() + expirationMillis);

        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(memberId))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(expiration);

        if (role != null && !role.isBlank()) {
            builder.claim(ROLE_CLAIM, role);
        }

        return builder
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(
            String token,
            AuthErrorCode expiredErrorCode,
            AuthErrorCode invalidErrorCode
    ) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthException(expiredErrorCode);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(invalidErrorCode);
        }
    }

    private void validateTokenType(Claims claims, String expectedTokenType) {
        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

        if (!expectedTokenType.equals(tokenType)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }
    }

    private Date now() {
        return Date.from(clock.instant());
    }
}
