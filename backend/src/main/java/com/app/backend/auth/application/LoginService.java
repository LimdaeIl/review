package com.app.backend.auth.application;

import com.app.backend.auth.application.command.LoginCommand;
import com.app.backend.auth.application.result.LoginResult;
import com.app.backend.auth.domain.Credential;
import com.app.backend.auth.domain.CredentialRepository;
import com.app.backend.auth.domain.RefreshTokenRepository;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import com.app.backend.auth.infrastructure.jwt.JWTHashUtil;
import com.app.backend.auth.infrastructure.jwt.JwtProvider;
import com.app.backend.member.application.MemberQueryService;
import com.app.backend.member.domain.Member;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LoginService {

    private final CredentialRepository credentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTHashUtil jwtHashUtil;
    private final JwtProvider jwtProvider;

    private final MemberQueryService memberQueryService;
    private final PasswordEncoder passwordEncoder;

    public LoginResult login(LoginCommand command) {
        Member member = memberQueryService.getByEmail(command.email());

        validateActiveMember(member);

        Credential credential = credentialRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_LOGIN));

        validatePassword(command.password(), credential.getEncodedPassword());

        String accessToken = jwtProvider.createAccessToken(
                member.getId(),
                member.getRole().name()
        );

        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        saveRefreshToken(member.getId(), refreshToken);

        return LoginResult.of(
                accessToken,
                refreshToken,
                jwtProvider.getRefreshTokenExpirationSeconds()
        );
    }

    private void validateActiveMember(Member member) {
        if (!member.isActive()) {
            throw new AuthException(AuthErrorCode.INACTIVE_MEMBER);
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthException(AuthErrorCode.INVALID_LOGIN);
        }
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        String hashedRefreshToken = jwtHashUtil.sha256(refreshToken);

        refreshTokenRepository.save(
                memberId,
                hashedRefreshToken,
                Duration.ofMillis(jwtProvider.getRefreshTokenExpirationMillis())
        );
    }
}
