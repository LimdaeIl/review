package com.app.backend.member.application;

import com.app.backend.member.application.command.CreateMemberCommand;
import com.app.backend.member.application.result.CreateMemberResult;
import com.app.backend.member.domain.Email;
import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.MemberRepository;
import com.app.backend.member.domain.Nickname;
import com.app.backend.member.domain.Phone;
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
            throw new IllegalArgumentException("회원: 이미 사용 중인 닉네임입니다.");
        }

        member.changeNickname(nickname);
    }


    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원: 존재하지 않는 회원입니다."));
    }

    private void validateDuplicate(Email email, Phone phone, Nickname nickname) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("회원: 이미 가입된 이메일입니다.");
        }

        if (phone != null && memberRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("회원: 이미 사용 중인 휴대폰 번호입니다.");
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("회원: 이미 사용 중인 닉네임입니다.");
        }
    }
}
