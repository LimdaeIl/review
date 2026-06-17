package com.app.backend.auth.presentation;

import com.app.backend.auth.application.OAuthLoginService;
import com.app.backend.auth.application.command.OAuthLoginCommand;
import com.app.backend.auth.application.result.LoginResult;
import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.infrastructure.cookie.RefreshTokenCookieProvider;
import com.app.backend.auth.infrastructure.oauth.OAuthProviderProperties;
import com.app.backend.auth.infrastructure.oauth.OAuthProviderProperties.Google;
import com.app.backend.auth.presentation.response.LoginResponse;
import com.app.backend.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
@RestController
public class OAuthController {

    private final OAuthLoginService oauthLoginService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;
    private final OAuthProviderProperties oauthProviderProperties;

    @GetMapping("/{provider}/callback")
    public CommonResponse<LoginResponse> callback(
            @PathVariable String provider,
            @RequestParam String code,
            HttpServletResponse response
    ) {
        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        LoginResult result = oauthLoginService.login(new OAuthLoginCommand(oauthProvider, code));
        refreshTokenCookieProvider.addRefreshTokenCookie(
                response,
                result.refreshToken(),
                result.refreshTokenMaxAgeSeconds()
        );

        return CommonResponse.success(
                "소셜 로그인: 로그인에 성공하였습니다.",
                LoginResponse.from(result)
        );
    }

    @GetMapping("/google/login")
    public void googleLogin(HttpServletResponse response) throws IOException {
        Google google = oauthProviderProperties.google();

        String url = google.authorizationUri()
                + "?client_id=" + encode(google.clientId())
                + "&redirect_uri=" + encode(google.redirectUri())
                + "&response_type=code"
                + "&scope=" + encode(google.scope());

        response.sendRedirect(url);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @GetMapping("/kakao/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        var kakao = oauthProviderProperties.kakao();

        String url = kakao.authorizationUri()
                + "?client_id=" + encode(kakao.clientId())
                + "&redirect_uri=" + encode(kakao.redirectUri())
                + "&response_type=code"
                + "&scope=" + encode(kakao.scope());

        response.sendRedirect(url);
    }

    @GetMapping("/github/login")
    public void githubLogin(HttpServletResponse response) throws IOException {
        var github = oauthProviderProperties.github();

        String url = github.authorizationUri()
                + "?client_id=" + encode(github.clientId())
                + "&redirect_uri=" + encode(github.redirectUri())
                + "&scope=" + encode(github.scope());

        response.sendRedirect(url);
    }
}
