package com.app.backend.common.exception;

import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<Object> parameters;

    public AppException(ErrorCode errorCode) {
        this(errorCode, List.of());
    }

    public AppException(ErrorCode errorCode, Object... parameters) {
        this(errorCode, toParameterList(parameters));
    }

    private AppException(ErrorCode errorCode, List<Object> parameters) {
        super(formatMessage(errorCode, parameters));
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    private static String formatMessage(ErrorCode errorCode, List<Object> parameters) {
        return Objects.requireNonNull(errorCode, "errorCode must not be null")
                .format(parameters.toArray());
    }

    private static List<Object> toParameterList(Object[] parameters) {
        return parameters == null ? List.of() : List.of(parameters);
    }
}
