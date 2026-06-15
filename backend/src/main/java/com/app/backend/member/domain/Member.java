package com.app.backend.member.domain;

import com.app.backend.member.exception.MemberErrorCode;
import com.app.backend.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_members")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Phone phone;

    @Embedded
    private Nickname nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    private Member(Email email, Phone phone, Nickname nickname, MemberRole role,
            MemberStatus status) {
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }

    public static Member create(Email email, Phone phone, Nickname nickname) {
        validate(email, nickname);

        return new Member(
                email,
                phone,
                nickname,
                MemberRole.MEMBER,
                MemberStatus.ACTIVE
        );
    }

    private static void validate(Email email, Nickname nickname) {
        if (email == null) {
            throw new MemberException(MemberErrorCode.INVALID_EMAIL);
        }

        if (nickname == null) {
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME);
        }
    }

    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public void changeRole(MemberRole role) {
        if (role == null) {
            throw new MemberException(MemberErrorCode.INVALID_ROLE);
        }

        this.role = role;
    }

    public void changeNickname(Nickname nickname) {
        if (nickname == null) {
            throw new MemberException(MemberErrorCode.INVALID_NICKNAME);
        }

        this.nickname = nickname;
    }
}
