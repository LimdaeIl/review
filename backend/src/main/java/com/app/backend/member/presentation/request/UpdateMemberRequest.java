package com.app.backend.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMemberRequest(
        @NotBlank
        String nickname
) {

}
