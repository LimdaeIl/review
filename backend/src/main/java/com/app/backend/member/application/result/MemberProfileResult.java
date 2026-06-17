package com.app.backend.member.application.result;

public record MemberProfileResult(
        Long memberId,
        String email,
        String nickname,
        String phoneNumber,
        String role
) {

}
