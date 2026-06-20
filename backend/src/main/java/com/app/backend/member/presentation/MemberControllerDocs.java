package com.app.backend.member.presentation;

import com.app.backend.auth.infrastructure.security.AuthMember;
import com.app.backend.common.response.CommonResponse;
import com.app.backend.member.presentation.request.ChangePasswordRequest;
import com.app.backend.member.presentation.request.UpdateMemberRequest;
import com.app.backend.member.presentation.response.MemberProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(
            summary = "내 프로필 조회",
            description = "현재 로그인한 회원의 프로필 정보를 조회합니다."
    )
    CommonResponse<MemberProfileResponse> getMyProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember
    );

    @Operation(
            summary = "내 프로필 수정",
            description = "현재 로그인한 회원의 닉네임 등 프로필 정보를 수정합니다."
    )
    CommonResponse<Void> updateProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody UpdateMemberRequest request
    );

    @Operation(
            summary = "비밀번호 변경",
            description = "현재 로그인한 회원의 비밀번호를 변경합니다."
    )
    CommonResponse<Void> changePassword(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody ChangePasswordRequest request
    );
}
