package com.app.backend.member.domain;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    boolean existsByEmail(Email email);

    boolean existsByPhone(Phone phone);

    boolean existsByNickname(Nickname nickname);

    Optional<Member> findById(Long id);
}
