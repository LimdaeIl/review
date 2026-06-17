package com.app.backend.auth.presentation;

import com.app.backend.auth.application.LoginService;
import com.app.backend.auth.application.LogoutService;
import com.app.backend.auth.application.ReissueTokenService;
import com.app.backend.auth.application.SignupService;
import com.app.backend.auth.application.result.LoginResult;
import com.app.backend.auth.application.result.ReissueTokenResult;
import com.app.backend.auth.application.result.SignupResult;
import com.app.backend.auth.infrastructure.cookie.RefreshTokenCookieProvider;
import com.app.backend.auth.presentation.request.LoginRequest;
import com.app.backend.auth.presentation.request.SignupRequest;
import com.app.backend.auth.presentation.response.LoginResponse;
import com.app.backend.auth.presentation.response.ReissueResponse;
import com.app.backend.auth.presentation.response.SignupResponse;
import com.app.backend.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
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
    private final ReissueTokenService reissueTokenService;
    private final LogoutService logoutService;

    @PostMapping("/signup")
    public CommonResponse<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResult result = signupService.signup(request.toCommand());

        return CommonResponse.created(
                "회원가입: 회원 가입이 완료되었습니다.",
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
                "로그인: 로그인에 성공하였습니다.",
                LoginResponse.from(result)
        );
    }

    @PostMapping("/reissue")
    public CommonResponse<ReissueResponse> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        ReissueTokenResult result = reissueTokenService.reissue(refreshToken);

        refreshTokenCookieProvider.addRefreshTokenCookie(
                response,
                result.refreshToken(),
                result.refreshTokenMaxAgeSeconds()
        );

        return CommonResponse.success(
                "토큰 재발급: 토큰 재발급에 성공하였습니다.",
                ReissueResponse.of(result.accessToken())
        );
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        logoutService.logout(refreshToken);

        refreshTokenCookieProvider.removeRefreshTokenCookie(response);

        return CommonResponse.success(
                "로그아웃: 로그아웃에 성공하였습니다.",
                null
        );
    }


    // TODO: 카카오 로그인 추가

}
