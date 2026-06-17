package com.app.backend.auth.infrastructure.security;

public record AuthMember(
        Long memberId,
        String role
) {

}
