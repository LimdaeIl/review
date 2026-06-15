package com.app.backend.auth.domain;

import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import com.app.backend.common.audit.BaseAuditEntity;
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
public class Credential extends BaseAuditEntity {

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
            throw new AuthException(AuthErrorCode.INVALID_MEMBER_ID);
        }

        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }
    }
}
