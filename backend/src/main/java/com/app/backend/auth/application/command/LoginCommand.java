package com.app.backend.auth.application.command;

public record LoginCommand(
        String email,
        String password
) {

}
