package com.app.backend.auth.infrastructure.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoResponse(
        String id,
        String email,

        @JsonProperty("verified_email")
        Boolean verifiedEmail,

        String name,
        String picture
) {
}
