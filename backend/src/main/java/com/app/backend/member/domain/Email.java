package com.app.backend.member.domain;

import com.app.backend.member.exception.MemberErrorCode;
import com.app.backend.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
            throw new MemberException(MemberErrorCode.INVALID_EMAIL);
        }

        if (value.length() > 100) {
            throw new MemberException(MemberErrorCode.INVALID_EMAIL_LENGTH);
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new MemberException(MemberErrorCode.INVALID_EMAIL_FORMAT);
        }
    }
}
