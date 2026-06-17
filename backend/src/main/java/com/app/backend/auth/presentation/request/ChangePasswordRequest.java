package com.app.backend.member.presentation.request;

import com.app.backend.auth.application.command.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
) {

    public ChangePasswordCommand toCommand(Long memberId) {
        return new ChangePasswordCommand(
                memberId,
                currentPassword,
                newPassword
        );
    }
}
