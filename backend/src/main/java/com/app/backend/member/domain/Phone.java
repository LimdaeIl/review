package com.app.backend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Phone {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^01[016789]\\d{7,8}$");

    @Column(name = "phone", unique = true, length = 11)
    private String phone;

    private Phone(String phone) {
        this.phone = phone;
    }

    public static Phone create(String value) {
        String normalized = normalize(value);

        if (normalized == null || normalized.isBlank()) {
            return null;
        }

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
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("회원: 올바른 휴대폰 번호 형식이 아닙니다.");
        }
    }
}
