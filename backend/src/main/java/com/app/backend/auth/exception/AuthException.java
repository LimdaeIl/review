package com.app.backend.auth.exception;

import com.app.backend.common.exception.AppException;

public class AuthException extends AppException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, Object... parameters) {
        super(errorCode, parameters);
    }
}
