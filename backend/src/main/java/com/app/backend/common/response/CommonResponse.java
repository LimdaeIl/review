package com.app.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final Clock CLOCK = Clock.system(SEOUL);

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(
                true,
                200,
                "성공: 요청이 성공적으로 처리되었습니다.",
                data,
                now()
        );
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>(
                true,
                200,
                message,
                data,
                now()
        );
    }

    public static <T> CommonResponse<T> success(String message) {
        return new CommonResponse<>(
                true,
                200,
                message,
                null,
                now()
        );
    }

    public static <T> CommonResponse<T> created(T data) {
        return new CommonResponse<>(
                true,
                201,
                "성공: 리소스가 생성되었습니다.",
                data,
                now()
        );
    }

    public static <T> CommonResponse<T> created(String message, T data) {
        return new CommonResponse<>(
                true,
                201,
                message,
                data,
                now()
        );
    }

    public static CommonResponse<Void> noContent() {
        return new CommonResponse<>(
                true,
                204,
                "성공: 요청이 성공적으로 처리되었습니다.",
                null,
                now()
        );
    }

    private static LocalDateTime now() {
        return LocalDateTime.now(CLOCK);
    }
}
