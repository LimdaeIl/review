package com.app.backend.member.presentation;

import com.app.backend.auth.application.ChangePasswordService;
import com.app.backend.auth.infrastructure.security.AuthMember;
import com.app.backend.common.response.CommonResponse;
import com.app.backend.member.application.MemberProfileService;
import com.app.backend.member.application.result.MemberProfileResult;
import com.app.backend.member.presentation.request.ChangePasswordRequest;
import com.app.backend.member.presentation.request.UpdateMemberRequest;
import com.app.backend.member.presentation.response.MemberProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController implements MemberControllerDocs {

    private final MemberProfileService memberProfileService;
    private final ChangePasswordService changePasswordService;

    @GetMapping("/me")
    public CommonResponse<MemberProfileResponse> getMyProfile(
            @AuthenticationPrincipal AuthMember authMember
    ) {
        MemberProfileResult result =
                memberProfileService.getProfile(authMember.memberId());

        return CommonResponse.success(
                "회원 조회: 회원 정보를 조회하였습니다.",
                MemberProfileResponse.from(result)
        );
    }

    @PatchMapping("/me")
    public CommonResponse<Void> updateProfile(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody UpdateMemberRequest request
    ) {
        memberProfileService.updateProfile(
                authMember.memberId(),
                request.nickname()
        );

        return CommonResponse.success(
                "회원 수정: 회원 정보가 수정되었습니다.",
                null
        );
    }

    @PatchMapping("/me/password")
    public CommonResponse<Void> changePassword(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        changePasswordService.changePassword(request.toCommand(authMember.memberId()));

        return CommonResponse.success(
                "비밀번호 변경: 비밀번호가 변경되었습니다.",
                null
        );
    }
}
