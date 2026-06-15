package com.app.backend.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus status();

    String message();

    default String code() {
        if (this instanceof Enum<?> enumCode) {
            return enumCode.name();
        }

        return getClass().getSimpleName();
    }

    default String format(Object... parameters) {
        if (parameters == null || parameters.length == 0) {
            return message();
        }

        return String.format(message(), parameters);
    }
}
