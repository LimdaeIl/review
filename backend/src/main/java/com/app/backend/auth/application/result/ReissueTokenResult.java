package com.app.backend.auth.application.result;

public record ReissueTokenResult(
        String accessToken,
        String refreshToken,
        long refreshTokenMaxAgeSeconds
) {

}
