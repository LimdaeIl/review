package com.app.backend.member.application.result;

import com.app.backend.member.domain.Member;

public record CreateMemberResult(
        Long memberId
) {

    public static CreateMemberResult from(Member member) {
        return new CreateMemberResult(
                member.getId()
        );
    }
}