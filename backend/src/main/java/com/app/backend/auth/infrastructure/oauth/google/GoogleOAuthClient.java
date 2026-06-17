package com.app.backend.auth.infrastructure.oauth.google;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.domain.OAuthUserInfo;
import com.app.backend.auth.infrastructure.oauth.OAuthClient;
import com.app.backend.auth.infrastructure.oauth.OAuthProviderProperties;
import com.app.backend.auth.infrastructure.oauth.OAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class GoogleOAuthClient implements OAuthClient {

    private final RestClient restClient;
    private final OAuthProviderProperties properties;

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public OAuthUserInfo getUserInfo(String code) {
        OAuthTokenResponse tokenResponse = requestAccessToken(code);
        GoogleUserInfoResponse userInfoResponse =
                requestUserInfo(tokenResponse.accessToken());

        return new OAuthUserInfo(
                OAuthProvider.GOOGLE,
                userInfoResponse.id(),
                userInfoResponse.email(),
                userInfoResponse.name(),
                Boolean.TRUE.equals(userInfoResponse.verifiedEmail())
        );
    }

    private OAuthTokenResponse requestAccessToken(String code) {
        OAuthProviderProperties.Google google = properties.google();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", google.clientId());
        formData.add("client_secret", google.clientSecret());
        formData.add("redirect_uri", google.redirectUri());
        formData.add("code", code);

        return restClient.post()
                .uri(google.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(OAuthTokenResponse.class);
    }

    private GoogleUserInfoResponse requestUserInfo(String accessToken) {
        OAuthProviderProperties.Google google = properties.google();

        return restClient.get()
                .uri(google.userInfoUri())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(GoogleUserInfoResponse.class);
    }
}
