package com.app.backend.member.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    DELETED("탈퇴");

    private final String description;

}
