package com.app.backend.member.application.result;

import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.MemberRole;
import com.app.backend.member.domain.MemberStatus;

public record MemberDetailResult(
        Long memberId,
        String email,
        String phone,
        String nickname,
        MemberRole role,
        MemberStatus status
) {

    public static MemberDetailResult from(Member member) {
        return new MemberDetailResult(
                member.getId(),
                member.getEmail().getEmail(),
                member.getPhone() == null ? null : member.getPhone().getPhone(),
                member.getNickname().getNickname(),
                member.getRole(),
                member.getStatus()
        );
    }
}
