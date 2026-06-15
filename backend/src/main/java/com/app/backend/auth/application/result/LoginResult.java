package com.app.backend.auth.application.result;

public record LoginResult(
        String accessToken,
        String refreshToken,
        long refreshTokenMaxAgeSeconds
) {

    public static LoginResult of(
            String accessToken,
            String refreshToken,
            long refreshTokenMaxAgeSeconds
    ) {
        return new LoginResult(
                accessToken,
                refreshToken,
                refreshTokenMaxAgeSeconds
        );
    }
}
