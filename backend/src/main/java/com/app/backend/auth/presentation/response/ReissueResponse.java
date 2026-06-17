package com.app.backend.auth.presentation.response;

public record ReissueResponse(
        String accessToken
) {

    public static ReissueResponse of(String accessToken) {
        return new ReissueResponse(accessToken);
    }
}
