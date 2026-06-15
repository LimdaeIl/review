package com.app.backend.member.infrastructure.persistence;

import com.app.backend.member.domain.Email;
import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.Nickname;
import com.app.backend.member.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(Email email);

    boolean existsByPhone(Phone phone);

    boolean existsByNickname(Nickname nickname);
}
