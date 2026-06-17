package com.app.backend.auth.application.command;

import com.app.backend.auth.domain.OAuthProvider;

public record OAuthLoginCommand(
        OAuthProvider provider,
        String code
) {


}