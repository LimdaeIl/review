package com.app.backend.member.domain;

import com.app.backend.member.exception.MemberErrorCode;
import com.app.backend.member.exception.MemberException;
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
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME);
        }

        if (value.length() > 12 || value.length() < 2) {
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME_LENGTH);
        }
    }
}
