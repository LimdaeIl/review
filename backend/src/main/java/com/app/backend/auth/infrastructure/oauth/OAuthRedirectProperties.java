package com.app.backend.auth.infrastructure.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oauth")
public record OAuthRedirectProperties(
        String frontendCallbackUrl
) {
}
