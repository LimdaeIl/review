package com.app.backend.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "OAuth", description = "OAuth 로그인 API")
public interface OAuthControllerDocs {

    @Operation(
            summary = "OAuth 로그인 페이지로 리다이렉트",
            description = """
                    provider에 해당하는 OAuth 인증 URL로 리다이렉트합니다.
                    
                    사용 예:
                    GET /api/v1/oauth/kakao/login
                    GET /api/v1/oauth/google/login
                    """
    )
    void login(
            @PathVariable String provider,
            HttpServletResponse response
    ) throws IOException;

    @Operation(
            summary = "OAuth 콜백 처리",
            description = """
                    OAuth provider에서 전달받은 authorization code로 로그인을 처리합니다.
                    Refresh Token은 쿠키에 저장하고,
                    Access Token은 프론트엔드 콜백 URL로 전달합니다.
                    """
    )
    void callback(
            @PathVariable String provider,
            @RequestParam String code,
            HttpServletResponse response
    ) throws IOException;
}
