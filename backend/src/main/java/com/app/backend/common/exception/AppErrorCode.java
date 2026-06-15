package com.app.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AppErrorCode implements ErrorCode {
    // 시스템/인프라
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "공통: 서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "공통: 서비스가 일시적으로 불가능합니다."),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "공통: 게이트웨이 응답 시간이 초과되었습니다."),
    DEPENDENCY_FAILURE(HttpStatus.BAD_GATEWAY, "공통: 외부/하위 시스템 연동에 실패했습니다."),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "공통: 입출력 처리 중 오류가 발생했습니다."),
    REQUEST_CONTEXT_NOT_FOUND(HttpStatus.NOT_FOUND, "공통: 요청 컨텍스트를 찾을 수 없습니다."),

    // 요청/형식/프로토콜
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "공통: 잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "공통: 허용되지 않은 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "공통: 지원하지 않는 콘텐츠 타입입니다."),

    // 리소스/상태
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "공통: 요청한 리소스를 찾을 수 없습니다."),
    RESP_BODY_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "공통: 응답 본문을 생성/쓰기 중 오류가 발생했습니다."),
    MEDIA_TYPE_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "공통: 요청한 응답 형식을 제공할 수 없습니다."),

    // 보안
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "공통: 인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "공통: 권한이 없습니다."),

    // 동시성/락
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "공통: 요청이 몰려 처리하지 못했습니다. 잠시 후 다시 시도해주세요."),
    LOCK_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "공통: 락 획득 중 인터럽트가 발생했습니다.");

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


