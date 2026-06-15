package com.app.backend.auth.presentation;

import com.app.backend.auth.application.LoginService;
import com.app.backend.auth.application.SignupService;
import com.app.backend.auth.application.result.LoginResult;
import com.app.backend.auth.application.result.SignupResult;
import com.app.backend.auth.infrastructure.cookie.RefreshTokenCookieProvider;
import com.app.backend.auth.presentation.request.LoginRequest;
import com.app.backend.auth.presentation.request.SignupRequest;
import com.app.backend.auth.presentation.response.LoginResponse;
import com.app.backend.auth.presentation.response.SignupResponse;
import com.app.backend.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @PostMapping("/signup")
    public CommonResponse<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResult result = signupService.signup(request.toCommand());

        return CommonResponse.created(
                "인증/인가: 회원 가입이 완료되었습니다.",
                SignupResponse.from(result)
        );
    }

    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult result = loginService.login(request.toCommand());

        refreshTokenCookieProvider.addRefreshTokenCookie(
                response,
                result.refreshToken(),
                result.refreshTokenMaxAgeSeconds()
        );

        return CommonResponse.success(
                "인증/인가: 로그인에 성공하였습니다.",
                LoginResponse.from(result)
        );
    }
}
