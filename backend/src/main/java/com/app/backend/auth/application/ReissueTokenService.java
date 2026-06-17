package com.app.backend.auth.application;

import com.app.backend.auth.application.result.ReissueTokenResult;
import com.app.backend.auth.domain.RefreshTokenRepository;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import com.app.backend.auth.infrastructure.jwt.JWTHashUtil;
import com.app.backend.auth.infrastructure.jwt.JwtProvider;
import com.app.backend.member.application.MemberCommandService;
import com.app.backend.member.domain.Member;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "ReissueTokenService")
@RequiredArgsConstructor
@Service
@Transactional
public class ReissueTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final JWTHashUtil jwtHashUtil;

    private final MemberCommandService memberCommandService;

    public ReissueTokenResult reissue(String refreshToken) {
        validateRefreshToken(refreshToken);

        Long memberId = jwtProvider.getMemberIdFromRefreshToken(refreshToken);
        Member member = memberCommandService.getMember(memberId);

        String newAccessToken = jwtProvider.createAccessToken(member.getId(),
                member.getRole().name());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());

        String hashedOldRefreshToken = jwtHashUtil.sha256(refreshToken);
        String hashedNewRefreshToken = jwtHashUtil.sha256(newRefreshToken);

        Duration refreshTokenTtl =
                Duration.ofMillis(jwtProvider.getRefreshTokenExpirationMillis());

        boolean rotated = refreshTokenRepository.rotateIfMatches(
                member.getId(),
                hashedOldRefreshToken,
                hashedNewRefreshToken,
                refreshTokenTtl
        );

        if (!rotated) {
            log.warn("Refresh token reuse detected. memberId={}", memberId);
            refreshTokenRepository.deleteByMemberId(memberId);
            throw new AuthException(AuthErrorCode.REUSED_REFRESH_TOKEN);
        }

        return new ReissueTokenResult(newAccessToken, newRefreshToken, refreshTokenTtl.toSeconds());
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }
    }
}

