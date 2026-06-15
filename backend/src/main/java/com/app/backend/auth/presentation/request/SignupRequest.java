package com.app.backend.auth.presentation.request;

import com.app.backend.auth.application.command.SignupCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "이메일: 이메일은 필수입니다.")
        @Email(message = "이메일: 올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호: 비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호: 비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        String password,

        String phone,

        @NotBlank(message = "닉네임: 닉네임은 필수입니다.")
        @Size(min = 2, max = 12, message = "닉네임: 닉네임은 2자 이상 12자 이하로 입력해주세요.")
        String nickname
) {

    public SignupCommand toCommand() {
        return new SignupCommand(
                email,
                password,
                phone,
                nickname
        );
    }
}