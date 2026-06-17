package com.app.backend.auth.infrastructure.oauth.github;

public record GithubUserInfoResponse(
        Long id,
        String login,
        String name,
        String email,
        String avatarUrl
) {
}
