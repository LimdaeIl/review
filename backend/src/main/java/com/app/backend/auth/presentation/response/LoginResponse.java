package com.app.backend.auth.presentation.response;

import com.app.backend.auth.application.result.LoginResult;

public record LoginResponse(
        String accessToken
) {

    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken());
    }
}
