package com.app.backend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    private Email(String email) {
        this.email = email;
    }

    public static Email create(String value) {
        String normalized = normalize(value);
        validate(normalized);

        return new Email(normalized);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("회원: 이메일은 필수입니다.");
        }

        if (value.length() > 100) {
            throw new IllegalArgumentException("회원: 이메일은 100자 이하로 입력해주세요.");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("회원: 올바른 이메일 형식이 아닙니다.");
        }
    }
}
