package com.app.backend.auth.application;

import com.app.backend.auth.application.command.ChangePasswordCommand;
import com.app.backend.auth.domain.Credential;
import com.app.backend.auth.domain.CredentialRepository;
import com.app.backend.auth.exception.AuthErrorCode;
import com.app.backend.auth.exception.AuthException;
import com.app.backend.member.application.MemberQueryService;
import com.app.backend.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ChangePasswordService {

    private final CredentialRepository credentialRepository;
    private final MemberQueryService memberQueryService;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(ChangePasswordCommand command) {
        Member member = memberQueryService.getById(command.memberId());

        if (!member.isActive()) {
            throw new AuthException(AuthErrorCode.INACTIVE_MEMBER);
        }

        Credential credential = credentialRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.PASSWORD_LOGIN_NOT_AVAILABLE));

        if (!passwordEncoder.matches(command.currentPassword(), credential.getEncodedPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        credential.changePassword(passwordEncoder.encode(command.newPassword()));
    }
}
