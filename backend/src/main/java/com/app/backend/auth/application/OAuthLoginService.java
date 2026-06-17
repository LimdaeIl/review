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
import java.util.Optional;
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

        Member member = findMemberBySocialAccount(userInfo)
                .orElseGet(() -> registerSocialMember(userInfo));

        validateActiveMember(member);

        return issueToken(member);
    }

    private Optional<Member> findMemberBySocialAccount(OAuthUserInfo userInfo) {
        return socialAccountRepository.findByProviderAndProviderUserId(
                        userInfo.provider(),
                        userInfo.providerUserId()
                )
                .map(socialAccount -> memberQueryService.getById(socialAccount.getMemberId()));
    }

    private Member registerSocialMember(OAuthUserInfo userInfo) {
        validateOAuthUserInfo(userInfo);
        validateEmailNotRegistered(userInfo.email());

        Member member = createMember(userInfo);
        createSocialAccount(member, userInfo);

        return member;
    }

    private void validateOAuthUserInfo(OAuthUserInfo userInfo) {
        if (!userInfo.hasEmail()) {
            throw new AuthException(AuthErrorCode.OAUTH_EMAIL_REQUIRED);
        }

        if (!userInfo.emailVerified()) {
            throw new AuthException(AuthErrorCode.OAUTH_EMAIL_NOT_VERIFIED);
        }
    }

    private void validateEmailNotRegistered(String emailValue) {
        Email email = Email.create(emailValue);

        if (memberQueryService.findByEmail(email).isPresent()) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_REGISTERED);
        }
    }

    private Member createMember(OAuthUserInfo userInfo) {
        var result = memberCommandService.create(
                CreateMemberCommand.builder()
                        .email(userInfo.email())
                        .nickname(resolveNickname(userInfo))
                        .phone(null)
                        .build()
        );

        return memberQueryService.getById(result.memberId());
    }

    private String resolveNickname(OAuthUserInfo userInfo) {
        if (userInfo.hasNickname()) {
            return userInfo.nickname();
        }

        return userInfo.provider().name().toLowerCase() + "_" + userInfo.providerUserId();
    }

    private void createSocialAccount(Member member, OAuthUserInfo userInfo) {
        SocialAccount socialAccount = SocialAccount.create(
                member.getId(),
                userInfo.provider(),
                userInfo.providerUserId(),
                userInfo.email()
        );

        socialAccountRepository.save(socialAccount);
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
