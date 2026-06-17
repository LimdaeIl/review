package com.app.backend.auth.application.command;

public record ChangePasswordCommand(
        Long memberId,
        String currentPassword,
        String newPassword
) {
}
