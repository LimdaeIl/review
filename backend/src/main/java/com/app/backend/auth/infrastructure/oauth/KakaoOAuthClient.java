package com.app.backend.auth.infrastructure.oauth;

import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.domain.OAuthUserInfo;
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

        if (userInfoResponse.getEmail() == null || userInfoResponse.getEmail().isBlank()) {
            throw new IllegalArgumentException("카카오 로그인: 이메일 제공 동의가 필요합니다.");
        }

        if (!Boolean.TRUE.equals(userInfoResponse.getEmailVerified())) {
            throw new IllegalArgumentException("카카오 로그인: 검증되지 않은 이메일입니다.");
        }

        return new OAuthUserInfo(
                OAuthProvider.KAKAO,
                userInfoResponse.getProviderUserId(),
                userInfoResponse.getEmail(),
                userInfoResponse.getNickname()
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
