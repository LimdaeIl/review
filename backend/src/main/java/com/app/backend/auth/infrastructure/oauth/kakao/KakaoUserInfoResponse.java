package com.app.backend.auth.infrastructure.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
        Long id,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {

    public record KakaoAccount(
            String email,

            @JsonProperty("is_email_verified")
            Boolean emailVerified,

            Profile profile
    ) {

    }

    public record Profile(
            String nickname
    ) {

    }

    public String getProviderUserId() {
        return String.valueOf(id);
    }

    public String getEmail() {
        return kakaoAccount == null ? null : kakaoAccount.email();
    }

    public Boolean getEmailVerified() {
        return kakaoAccount == null ? null : kakaoAccount.emailVerified();
    }

    public String getNickname() {
        if (kakaoAccount == null || kakaoAccount.profile() == null) {
            return "kakaoUser";
        }

        return kakaoAccount.profile().nickname();
    }
}
