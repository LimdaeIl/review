package com.app.backend.auth.domain;

public record OAuthUserInfo(
        OAuthProvider provider,
        String providerUserId,
        String email,
        String nickname
) {

}
