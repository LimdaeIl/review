package com.app.backend.member.infrastructure.persistence;

import com.app.backend.member.domain.Email;
import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.MemberRepository;
import com.app.backend.member.domain.Nickname;
import com.app.backend.member.domain.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final JpaMemberRepository repository;

    @Override
    public Member save(Member member) {
        return repository.save(member);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(Phone phone) {
        return repository.existsByPhone(phone);
    }

    @Override
    public boolean existsByNickname(Nickname nickname) {
        return repository.existsByNickname(nickname);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return repository.findById(id);
    }
}
