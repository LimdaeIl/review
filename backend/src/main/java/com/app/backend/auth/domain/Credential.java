package com.app.backend.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_credentials")
@Entity
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;

    @Column(name = "password", nullable = false)
    private String encodedPassword;

    private Credential(Long memberId, String encodedPassword) {
        this.memberId = memberId;
        this.encodedPassword = encodedPassword;
    }

    public static Credential create(Long memberId, String encodedPassword) {
        validate(memberId, encodedPassword);

        return new Credential(memberId, encodedPassword);
    }

    public static void validate(Long memberId, String encodedPassword) {
        if (memberId == null) {
            throw new IllegalArgumentException("인증: 회원 ID는 필수입니다.");
        }

        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("인증: 비밀번호는 필수입니다.");
        }
    }
}
