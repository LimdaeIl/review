package com.app.backend.member.presentation.request;

import com.app.backend.auth.application.command.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
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
