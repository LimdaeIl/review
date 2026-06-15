package com.app.backend.member.application;

import com.app.backend.auth.application.result.LoginResult;
import com.app.backend.member.application.command.CreateMemberCommand;
import com.app.backend.member.application.result.CreateMemberResult;
import com.app.backend.member.domain.Email;
import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.MemberRepository;
import com.app.backend.member.domain.Nickname;
import com.app.backend.member.domain.Phone;
import com.app.backend.member.exception.MemberErrorCode;
import com.app.backend.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

    private final MemberRepository memberRepository;

    public CreateMemberResult create(CreateMemberCommand command) {
        Email email = Email.create(command.email());
        Phone phone = Phone.create(command.phone());
        Nickname nickname = Nickname.create(command.nickname());

        validateDuplicate(email, phone, nickname);

        Member member = Member.create(email, phone, nickname);
        Member savedMember = memberRepository.save(member);

        return CreateMemberResult.from(savedMember);
    }

    public void changeNickname(Long memberId, String nicknameValue) {
        Member member = getMember(memberId);
        Nickname nickname = Nickname.create(nicknameValue);

        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        member.changeNickname(nickname);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDuplicate(Email email, Phone phone, Nickname nickname) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL);
        }

        if (phone != null && memberRepository.existsByPhone(phone)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_PHONE);
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
