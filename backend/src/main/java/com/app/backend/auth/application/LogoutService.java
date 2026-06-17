package com.app.backend.auth.application;

import com.app.backend.auth.domain.RefreshTokenRepository;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import com.app.backend.auth.infrastructure.jwt.JWTHashUtil;
import com.app.backend.auth.infrastructure.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final JWTHashUtil jwtHashUtil;

    public void logout(String refreshToken) {
        validateRefreshToken(refreshToken);

        Long memberId = jwtProvider.getMemberIdFromRefreshToken(refreshToken);
        String hashedRefreshToken = jwtHashUtil.sha256(refreshToken);

        String storedRefreshTokenHash = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EXPIRED_REFRESH_TOKEN));

        if (!storedRefreshTokenHash.equals(hashedRefreshToken)) {
            throw new AuthException(AuthErrorCode.REUSED_REFRESH_TOKEN);
        }

        refreshTokenRepository.deleteByMemberId(memberId);
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }
    }
}
