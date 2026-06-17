package com.app.backend.auth.domain;

public record OAuthUserInfo(
        OAuthProvider provider,
        String providerUserId,
        String email,
        String nickname,
        boolean emailVerified
) {

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasNickname() {
        return nickname != null && !nickname.isBlank();
    }
}
