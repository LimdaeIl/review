package com.app.backend.member.application;

import com.app.backend.member.domain.Email;
import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.MemberRepository;
import com.app.backend.member.exception.MemberErrorCode;
import com.app.backend.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public Member getByEmail(String emailValue) {
        Email email = Email.create(emailValue);

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
