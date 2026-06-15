package com.app.backend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Nickname {

    @Column(name = "nickname", unique = true, nullable = false, length = 12)
    private String nickname;

    private Nickname(String nickname) {
        this.nickname = nickname;
    }

    public static Nickname create(String value) {
        String normalized = normalize(value);
        validate(normalized);

        return new Nickname(normalized);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("회원: 닉네임은 필수입니다.");
        }

        if (value.length() > 12 || value.length() < 2) {
            throw new IllegalArgumentException("회원: 닉네임은 2자 이상 12자 이하로 입력해주세요.");
        }
    }
}
