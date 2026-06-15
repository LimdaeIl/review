package com.app.backend.common.exception;

import com.app.backend.common.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j(topic = "CustomExceptionHandler")
@RestControllerAdvice
public class CustomExceptionHandler {

    private static final String PROBLEM_BASE_URI = "about:blank/";

    // 비즈니스 예외를 ErrorCode 기반 응답으로 변환한다.
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleApp(AppException ex, HttpServletRequest request) {
        ErrorCode code = ex.getErrorCode();
        return error(code, ex.getMessage(), request, ex.getParameters(), null);
    }

    // RequestBody 검증 실패 예외를 필드 오류 목록으로 변환한다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.INVALID_INPUT_VALUE, request, extractFieldErrors(ex));
    }

    // RequestParam, PathVariable 등 단일 값 검증 실패 예외를 필드 오류 목록으로 변환한다.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.INVALID_INPUT_VALUE, request, extractFieldErrors(ex));
    }

    // 필수 RequestParam 누락 예외를 잘못된 입력 응답으로 변환한다.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse.ValidationError> errors = List.of(
                ErrorResponse.ValidationError.of(ex.getParameterName(), "필수 요청 파라미터입니다.")
        );

        return appError(AppErrorCode.INVALID_INPUT_VALUE, request, errors);
    }

    // RequestParam, PathVariable 타입 변환 실패 예외를 잘못된 입력 응답으로 변환한다.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse.ValidationError> errors = List.of(
                ErrorResponse.ValidationError.of(ex.getName(), "요청 값의 타입이 올바르지 않습니다.")
        );

        return appError(AppErrorCode.INVALID_INPUT_VALUE, request, errors);
    }

    // 읽을 수 없는 요청 본문 형식 예외를 잘못된 입력 응답으로 변환한다.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.INVALID_INPUT_VALUE, request);
    }

    // 지원하지 않는 HTTP Method 요청을 Method Not Allowed 응답으로 변환한다.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.METHOD_NOT_ALLOWED, request);
    }

    // JPA 엔티티 조회 실패 예외를 Entity Not Found 응답으로 변환한다.
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.ENTITY_NOT_FOUND, request);
    }

    // 인가 실패 예외를 Access Denied 응답으로 변환한다.
    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            Exception ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.ACCESS_DENIED, request);
    }

    // 인증 실패 예외를 Unauthorized 응답으로 변환한다.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return appError(AppErrorCode.UNAUTHORIZED, request);
    }

    // 처리되지 않은 예외를 로깅하고 Internal Server Error 응답으로 변환한다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("처리되지 않은 예외", ex);
        return appError(AppErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    // RequestBody 검증 실패 항목을 공통 필드 오류 형식으로 변환한다.
    private List<ErrorResponse.ValidationError> extractFieldErrors(
            MethodArgumentNotValidException ex
    ) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.ValidationError.of(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();
    }

    // ConstraintViolation 검증 실패 항목을 공통 필드 오류 형식으로 변환한다.
    private List<ErrorResponse.ValidationError> extractFieldErrors(
            ConstraintViolationException ex
    ) {
        return ex.getConstraintViolations()
                .stream()
                .map(violation -> ErrorResponse.ValidationError.of(
                        extractFieldName(violation.getPropertyPath().toString()),
                        violation.getMessage()
                ))
                .toList();
    }

    // 프로퍼티 경로에서 마지막 필드명만 추출한다.
    private String extractFieldName(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        return lastDotIndex == -1 ? path : path.substring(lastDotIndex + 1);
    }

    // 애플리케이션 기본 오류 응답을 생성한다.
    private ResponseEntity<ErrorResponse> appError(
            AppErrorCode code,
            HttpServletRequest request
    ) {
        return error(code, code.message(), request, null, null);
    }

    // 필드 오류 목록이 포함된 애플리케이션 오류 응답을 생성한다.
    private ResponseEntity<ErrorResponse> appError(
            AppErrorCode code,
            HttpServletRequest request,
            List<ErrorResponse.ValidationError> errors
    ) {
        return error(code, code.message(), request, null, errors);
    }

    // ErrorCode 정보를 RFC 7807 형태의 공통 오류 응답으로 조립한다.
    private ResponseEntity<ErrorResponse> error(
            ErrorCode code,
            String detail,
            HttpServletRequest request,
            List<Object> parameters,
            List<ErrorResponse.ValidationError> errors
    ) {
        String title = code.code();

        return ResponseEntity.status(code.status())
                .body(ErrorResponse.of(
                        problemType(title),
                        title,
                        code.status(),
                        detail,
                        request.getRequestURI(),
                        title,
                        parameters,
                        errors
                ));
    }

    // ErrorCode 이름을 problem type URI 형식으로 변환한다.
    private String problemType(String title) {
        return PROBLEM_BASE_URI + title.toLowerCase().replace('_', '-');
    }
}
