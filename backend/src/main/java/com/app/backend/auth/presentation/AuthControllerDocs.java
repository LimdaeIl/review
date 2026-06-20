package com.app.backend.auth.presentation;

import com.app.backend.auth.presentation.request.LoginRequest;
import com.app.backend.auth.presentation.request.SignupRequest;
import com.app.backend.auth.presentation.response.LoginResponse;
import com.app.backend.auth.presentation.response.ReissueResponse;
import com.app.backend.auth.presentation.response.SignupResponse;
import com.app.backend.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호 등 회원 정보를 입력받아 회원가입을 처리합니다."
    )
    CommonResponse<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    );

    @Operation(
            summary = "로그인",
            description = """
                    이메일과 비밀번호로 로그인을 처리합니다.
                    Access Token은 응답 바디로 반환하고,
                    Refresh Token은 HttpOnly Cookie로 저장합니다.
                    """
    )
    CommonResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "토큰 재발급",
            description = """
                    Refresh Token Cookie를 사용하여 Access Token을 재발급합니다.
                    새 Refresh Token도 Cookie로 다시 저장합니다.
                    """
    )
    CommonResponse<ReissueResponse> reissue(
            @Parameter(hidden = true)
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    );

    @Operation(
            summary = "로그아웃",
            description = """
                    Refresh Token을 무효화하고 Refresh Token Cookie를 제거합니다.
                    """
    )
    CommonResponse<Void> logout(
            @Parameter(hidden = true)
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    );
}
