package com.app.backend.auth.exception;

import com.app.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "인증: 회원 ID는 필수입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "인증: 비밀번호는 필수입니다."),

    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "인증: 이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "인증: Access Token이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "인증: Refresh Token이 유효하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "인증: Access Token이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "인증: Refresh Token이 만료되었습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "인증: 토큰 타입이 올바르지 않습니다."),
    INACTIVE_MEMBER(HttpStatus.FORBIDDEN, "인증: 비활성화된 회원입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "인증: Refresh Token이 없습니다."),

    REUSED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "인증: 재사용된 Refresh Token입니다."),
    TOKEN_STORE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "인증: 토큰 저장소를 사용할 수 없습니다."),
    REFRESH_TOKEN_REPLACED(HttpStatus.UNAUTHORIZED, "인증: 다른 환경에서 로그인되어 Refresh Token이 만료되었습니다."),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "소셜 로그인: 지원하지 않는 provider입니다."),;

    private final HttpStatus httpStatus;
    private final String messageTemplate;

    @Override
    public HttpStatus status() {
        return httpStatus;
    }

    @Override
    public String message() {
        return messageTemplate;
    }
}
