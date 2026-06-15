package com.app.backend.member.domain;

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
    private MemberStatus status;

    private Member(Email email, Phone phone, Nickname nickname, MemberStatus status) {
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.status = status;
    }

    public static Member create(Email email, Phone phone, Nickname nickname) {
        validate(email, nickname);

        return new Member(
                email,
                phone,
                nickname,
                MemberStatus.ACTIVE
        );
    }

    private static void validate(Email email, Nickname nickname) {
        if (email == null) {
            throw new IllegalArgumentException("회원: 이메일은 필수입니다.");
        }

        if (nickname == null) {
            throw new IllegalArgumentException("회원: 닉네임은 필수입니다.");
        }
    }


    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

}
