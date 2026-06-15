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
public class Phone {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^01[016789]\\d{7,8}$");

    @Column(name = "phone", nullable = false, unique = true, length = 11)
    private String phone;

    private Phone(String phone) {
        this.phone = phone;
    }

    public static Phone create(String value) {
        String normalized = normalize(value);
        validate(normalized);

        return new Phone(normalized);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        return value
                .trim()
                .replace("-", "")
                .replace(" ", "");
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("회원: 휴대폰 번호는 필수입니다.");
        }

        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("회원: 올바른 휴대폰 번호 형식이 아닙니다.");
        }
    }
}
