package com.app.backend.member.presentation.response;

import com.app.backend.member.application.result.MemberProfileResult;

public record MemberProfileResponse(
        Long memberId,
        String email,
        String nickname,
        String phoneNumber,
        String role
) {

    public static MemberProfileResponse from(MemberProfileResult result) {
        return new MemberProfileResponse(
                result.memberId(),
                result.email(),
                result.nickname(),
                result.phoneNumber(),
                result.role()
        );
    }
}
