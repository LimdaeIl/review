package com.app.backend.member.exception;

import com.app.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원: 존재하지 않는 회원입니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "회원: 이미 가입된 이메일입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "회원: 이미 사용 중인 휴대폰 번호입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "회원: 이미 사용 중인 닉네임입니다."),

    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "회원: 이메일은 필수입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "회원: 올바른 이메일 형식이 아닙니다."),
    INVALID_EMAIL_LENGTH(HttpStatus.BAD_REQUEST, "회원: 이메일은 100자 이하로 입력해주세요."),

    INVALID_PHONE(HttpStatus.BAD_REQUEST, "회원: 휴대폰 번호는 필수입니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "회원: 올바른 휴대폰 번호 형식이 아닙니다."),

    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "회원: 닉네임은 필수입니다."),
    INVALID_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "회원: 닉네임은 2자 이상 12자 이하로 입력해주세요."),

    INVALID_ROLE(HttpStatus.BAD_REQUEST, "회원: 권한은 필수입니다.");

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
