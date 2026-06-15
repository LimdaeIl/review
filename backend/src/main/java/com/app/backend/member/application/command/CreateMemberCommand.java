package com.app.backend.member.application.command;

import lombok.Builder;

@Builder
public record CreateMemberCommand(
        String email,
        String phone,
        String nickname
) {


}