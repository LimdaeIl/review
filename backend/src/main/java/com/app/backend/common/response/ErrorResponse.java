package com.app.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String errorCode,
        List<Object> parameters,
        List<ValidationError> errors
) {

    public static ErrorResponse of(
            String type,
            String title,
            HttpStatus status,
            String detail,
            String instance,
            String errorCode
    ) {
        return of(type, title, status, detail, instance, errorCode, null, null);
    }

    public static ErrorResponse of(
            String type,
            String title,
            HttpStatus status,
            String detail,
            String instance,
            String errorCode,
            List<Object> parameters,
            List<ValidationError> errors
    ) {
        return new ErrorResponse(
                type,
                title,
                status.value(),
                detail,
                instance,
                errorCode,
                emptyToNull(parameters),
                emptyToNull(errors)
        );
    }

    private static <T> List<T> emptyToNull(List<T> values) {
        return values == null || values.isEmpty() ? null : List.copyOf(values);
    }

    public record ValidationError(
            String field,
            String reason
    ) {

        public static ValidationError of(String field, String reason) {
            return new ValidationError(field, reason);
        }
    }
}