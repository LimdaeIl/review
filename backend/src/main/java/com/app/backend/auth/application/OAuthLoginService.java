package com.app.backend.auth.application;

import com.app.backend.auth.application.command.OAuthLoginCommand;
import com.app.backend.auth.application.result.LoginResult;
import com.app.backend.auth.domain.OAuthUserInfo;
import com.app.backend.auth.domain.RefreshTokenRepository;
import com.app.backend.auth.domain.SocialAccount;
import com.app.backend.auth.domain.SocialAccountRepository;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import com.app.backend.auth.infrastructure.jwt.JWTHashUtil;
import com.app.backend.auth.infrastructure.jwt.JwtProvider;
import com.app.backend.auth.infrastructure.oauth.OAuthClient;
import com.app.backend.auth.infrastructure.oauth.OAuthClientResolver;
import com.app.backend.member.application.MemberCommandService;
import com.app.backend.member.application.MemberQueryService;
import com.app.backend.member.application.command.CreateMemberCommand;
import com.app.backend.member.domain.Email;
import com.app.backend.member.domain.Member;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OAuthLoginService {

    private final OAuthClientResolver oauthClientResolver;
    private final SocialAccountRepository socialAccountRepository;

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    private final JwtProvider jwtProvider;
    private final JWTHashUtil jwtHashUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResult login(OAuthLoginCommand command) {
        OAuthClient client = oauthClientResolver.resolve(command.provider());
        OAuthUserInfo userInfo = client.getUserInfo(command.code());
        Member member = findOrCreateMember(userInfo);
        validateActiveMember(member);

        return issueToken(member);
    }

    private Member findOrCreateMember(OAuthUserInfo userInfo) {
        return socialAccountRepository.findByProviderAndProviderUserId(userInfo.provider(),
                        userInfo.providerUserId())
                .map(socialAccount -> memberQueryService.getById(socialAccount.getMemberId()))
                .orElseGet(() -> linkOrCreateMember(userInfo));
    }

    private Member linkOrCreateMember(OAuthUserInfo userInfo) {
        Email email = Email.create(userInfo.email());

        return memberQueryService.findByEmail(email)
                .map(member -> linkSocialAccount(member, userInfo))
                .orElseGet(() -> createMemberWithSocialAccount(userInfo));
    }

    private Member linkSocialAccount(Member member, OAuthUserInfo userInfo) {
        SocialAccount socialAccount = SocialAccount.create(
                member.getId(),
                userInfo.provider(),
                userInfo.providerUserId(),
                userInfo.email()
        );

        socialAccountRepository.save(socialAccount);

        return member;
    }

    private Member createMemberWithSocialAccount(OAuthUserInfo userInfo) {
        var result = memberCommandService.create(
                CreateMemberCommand.builder()
                        .email(userInfo.email())
                        .nickname(userInfo.nickname())
                        .phone(null)
                        .build()
        );

        Member member = memberQueryService.getById(result.memberId());

        SocialAccount socialAccount = SocialAccount.create(
                member.getId(),
                userInfo.provider(),
                userInfo.providerUserId(),
                userInfo.email()
        );

        socialAccountRepository.save(socialAccount);

        return member;
    }

    private void validateActiveMember(Member member) {
        if (!member.isActive()) {
            throw new AuthException(AuthErrorCode.INACTIVE_MEMBER);
        }
    }

    private LoginResult issueToken(Member member) {
        String accessToken = jwtProvider.createAccessToken(
                member.getId(),
                member.getRole().name()
        );

        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        String hashedRefreshToken = jwtHashUtil.sha256(refreshToken);

        refreshTokenRepository.save(
                member.getId(),
                hashedRefreshToken,
                Duration.ofMillis(jwtProvider.getRefreshTokenExpirationMillis())
        );

        return LoginResult.of(
                accessToken,
                refreshToken,
                jwtProvider.getRefreshTokenExpirationSeconds()
        );
    }
}
