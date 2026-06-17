package com.app.backend.member.application;

import com.app.backend.member.application.result.MemberProfileResult;
import com.app.backend.member.domain.Member;
import com.app.backend.member.domain.MemberRepository;
import com.app.backend.member.domain.Nickname;
import com.app.backend.member.exception.MemberErrorCode;
import com.app.backend.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberProfileService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberProfileResult getProfile(Long memberId) {
        Member member = getMember(memberId);

        return new MemberProfileResult(
                member.getId(),
                member.getEmail().getValue(),
                member.getNickname().getValue(),
                member.getPhone() == null ? null : member.getPhone().getValue(),
                member.getRole().name()
        );
    }

    public void updateProfile(Long memberId, String nicknameValue) {
        Member member = getMember(memberId);

        Nickname nickname = Nickname.create(nicknameValue);
        member.changeNickname(nickname);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
