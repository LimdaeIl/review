package com.app.backend.auth.application;

import com.app.backend.auth.application.command.SignupCommand;
import com.app.backend.auth.application.result.SignupResult;
import com.app.backend.auth.domain.Credential;
import com.app.backend.auth.domain.CredentialRepository;
import com.app.backend.member.application.MemberCommandService;
import com.app.backend.member.application.command.CreateMemberCommand;
import com.app.backend.member.application.result.CreateMemberResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SignupService {

    private final CredentialRepository credentialRepository;

    private final MemberCommandService memberCommandService;
    private final PasswordEncoder passwordEncoder;

    public SignupResult signup(SignupCommand command) {
        CreateMemberResult member = memberCommandService.create(
                CreateMemberCommand.builder()
                        .email(command.email())
                        .nickname(command.nickname())
                        .phone(command.phone())
                        .build()
        );

        Credential credential = Credential.create(
                member.memberId(),
                passwordEncoder.encode(command.password())
        );

        credentialRepository.save(credential);

        return new SignupResult(member.memberId());
    }
}
