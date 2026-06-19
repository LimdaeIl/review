package com.app.backend.auth.presentation;

import com.app.backend.auth.application.OAuthLoginService;
import com.app.backend.auth.application.OAuthRedirectService;
import com.app.backend.auth.application.command.OAuthLoginCommand;
import com.app.backend.auth.domain.OAuthProvider;
import com.app.backend.auth.infrastructure.cookie.RefreshTokenCookieProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final OAuthRedirectService oauthRedirectService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @GetMapping("/{provider}/login")
    public void login(
            @PathVariable String provider,
            HttpServletResponse response
    ) throws IOException {
        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        response.sendRedirect(oauthRedirectService.getAuthorizationUrl(oauthProvider));
    }

    @GetMapping("/{provider}/callback")
    public void callback(
            @PathVariable String provider,
            @RequestParam String code,
            HttpServletResponse response
    ) throws IOException {
        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        var result = oauthLoginService.login(new OAuthLoginCommand(oauthProvider, code));
        refreshTokenCookieProvider.addRefreshTokenCookie(
                response,
                result.refreshToken(),
                result.refreshTokenMaxAgeSeconds()
        );
        response.sendRedirect(oauthRedirectService.getFrontendCallbackUrl(
                result.accessToken()
        ));
    }
}
