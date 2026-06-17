package com.app.backend.auth.infrastructure.oauth.kakao;

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
public class KakaoOAuthClient implements OAuthClient {

    private final RestClient restClient;
    private final OAuthProviderProperties properties;

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public OAuthUserInfo getUserInfo(String code) {
        OAuthTokenResponse tokenResponse = requestAccessToken(code);
        KakaoUserInfoResponse userInfoResponse =
                requestUserInfo(tokenResponse.accessToken());

        return new OAuthUserInfo(
                OAuthProvider.KAKAO,
                userInfoResponse.getProviderUserId(),
                userInfoResponse.getEmail(),
                userInfoResponse.getNickname(),
                Boolean.TRUE.equals(userInfoResponse.getEmailVerified())
        );
    }

    private OAuthTokenResponse requestAccessToken(String code) {
        OAuthProviderProperties.Kakao kakao = properties.kakao();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakao.clientId());
        formData.add("redirect_uri", kakao.redirectUri());
        formData.add("code", code);

        if (kakao.clientSecret() != null && !kakao.clientSecret().isBlank()) {
            formData.add("client_secret", kakao.clientSecret());
        }

        return restClient.post()
                .uri(kakao.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(OAuthTokenResponse.class);
    }

    private KakaoUserInfoResponse requestUserInfo(String accessToken) {
        OAuthProviderProperties.Kakao kakao = properties.kakao();

        return restClient.get()
                .uri(kakao.userInfoUri())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }
}
