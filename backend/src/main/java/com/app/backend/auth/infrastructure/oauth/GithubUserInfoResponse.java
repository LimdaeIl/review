package com.app.backend.auth.infrastructure.oauth;

public record GithubUserInfoResponse(
        Long id,
        String login,
        String name,
        String email,
        String avatarUrl
) {
}
