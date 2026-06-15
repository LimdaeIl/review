package com.app.backend.member.application.command;

public record ChangeNicknameCommand(
        Long memberId,
        String nickname
) {

}
