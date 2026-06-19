package com.app.backend.auth.application;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.infrastructure.oauth.OAuthProviderProperties;
import com.app.backend.auth.infrastructure.oauth.OAuthRedirectProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuthRedirectService {

    private final OAuthProviderProperties oauthProviderProperties;
    private final OAuthRedirectProperties oauthRedirectProperties;

    public String getAuthorizationUrl(OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> buildAuthorizationUrl(
                    oauthProviderProperties.google().authorizationUri(),
                    oauthProviderProperties.google().clientId(),
                    oauthProviderProperties.google().redirectUri(),
                    oauthProviderProperties.google().scope()
            );
            case KAKAO -> buildAuthorizationUrl(
                    oauthProviderProperties.kakao().authorizationUri(),
                    oauthProviderProperties.kakao().clientId(),
                    oauthProviderProperties.kakao().redirectUri(),
                    oauthProviderProperties.kakao().scope()
            );
            case GITHUB -> buildAuthorizationUrl(
                    oauthProviderProperties.github().authorizationUri(),
                    oauthProviderProperties.github().clientId(),
                    oauthProviderProperties.github().redirectUri(),
                    oauthProviderProperties.github().scope()
            );
        };
    }

    public String getFrontendCallbackUrl(String accessToken) {
        return oauthRedirectProperties.frontendCallbackUrl()
                + "?accessToken=" + encode(accessToken);
    }

    private String buildAuthorizationUrl(
            String authorizationUri,
            String clientId,
            String redirectUri,
            String scope
    ) {
        return authorizationUri
                + "?client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(redirectUri)
                + "&response_type=code"
                + "&scope=" + encode(scope);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
