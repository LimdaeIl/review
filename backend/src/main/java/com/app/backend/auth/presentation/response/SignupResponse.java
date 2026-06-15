package com.app.backend.auth.presentation.response;

import com.app.backend.auth.application.result.SignupResult;

public record SignupResponse(
        Long memberId
) {

    public static SignupResponse from(SignupResult result) {
        return new SignupResponse(
                result.memberId()
        );
    }
}
