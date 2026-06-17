package com.app.backend.auth.infrastructure.oauth.github;

public record GithubEmailResponse(
        String email,
        Boolean primary,
        Boolean verified,
        String visibility
) {
}
