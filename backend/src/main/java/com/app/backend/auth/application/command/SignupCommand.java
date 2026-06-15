package com.app.backend.auth.application.command;

public record SignupCommand(
        String email,
        String password,
        String phone,
        String nickname
) {

}
