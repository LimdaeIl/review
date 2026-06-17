package com.app.backend.auth.infrastructure.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth")
public record OAuthProviderProperties(
        Google google,
        Kakao kakao,
        Github github
) {

    public record Google(
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String scope
    ) {
    }

    public record Kakao(
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String scope
    ) {
    }

    public record Github(
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizationUri,
            String tokenUri,
            String userInfoUri,
            String emailUri,
            String scope
    ) {
    }
}