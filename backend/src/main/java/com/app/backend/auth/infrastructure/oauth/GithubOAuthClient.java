package com.app.backend.auth.infrastructure.oauth;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.domain.OAuthUserInfo;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class GithubOAuthClient implements OAuthClient {

    private final RestClient restClient;
    private final OAuthProviderProperties properties;

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.GITHUB;
    }

    @Override
    public OAuthUserInfo getUserInfo(String code) {
        OAuthTokenResponse tokenResponse = requestAccessToken(code);
        GithubUserInfoResponse userInfoResponse =
                requestUserInfo(tokenResponse.accessToken());

        String email = resolveEmail(
                tokenResponse.accessToken(),
                userInfoResponse.email()
        );

        String nickname = resolveNickname(userInfoResponse);

        return new OAuthUserInfo(
                OAuthProvider.GITHUB,
                String.valueOf(userInfoResponse.id()),
                email,
                nickname
        );
    }

    private OAuthTokenResponse requestAccessToken(String code) {
        OAuthProviderProperties.Github github = properties.github();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", github.clientId());
        formData.add("client_secret", github.clientSecret());
        formData.add("redirect_uri", github.redirectUri());
        formData.add("code", code);

        return restClient.post()
                .uri(github.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(formData)
                .retrieve()
                .body(OAuthTokenResponse.class);
    }

    private GithubUserInfoResponse requestUserInfo(String accessToken) {
        OAuthProviderProperties.Github github = properties.github();

        return restClient.get()
                .uri(github.userInfoUri())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GithubUserInfoResponse.class);
    }

    private String resolveEmail(String accessToken, String userEmail) {
        if (userEmail != null && !userEmail.isBlank()) {
            return userEmail;
        }

        GithubEmailResponse[] emails = requestEmails(accessToken);

        return Arrays.stream(emails)
                .filter(email -> Boolean.TRUE.equals(email.primary()))
                .filter(email -> Boolean.TRUE.equals(email.verified()))
                .map(GithubEmailResponse::email)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "깃허브 로그인: 검증된 기본 이메일이 필요합니다."
                ));
    }

    private GithubEmailResponse[] requestEmails(String accessToken) {
        OAuthProviderProperties.Github github = properties.github();

        return restClient.get()
                .uri(github.emailUri())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GithubEmailResponse[].class);
    }

    private String resolveNickname(GithubUserInfoResponse userInfoResponse) {
        if (userInfoResponse.name() != null && !userInfoResponse.name().isBlank()) {
            return userInfoResponse.name();
        }

        return userInfoResponse.login();
    }
}
