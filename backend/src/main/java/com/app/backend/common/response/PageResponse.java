package com.app.backend.common.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
        List<T> content,
        PageInfo page
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                PageInfo.from(page)
        );
    }

    public static <T> PageResponse<T> of(List<T> content, Page<?> page) {
        return new PageResponse<>(
                content,
                PageInfo.from(page)
        );
    }

    public record PageInfo(
            int page,
            int size,
            int numberOfElements,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last,
            boolean empty
    ) {

        private static PageInfo from(Page<?> page) {
            return new PageInfo(
                    page.getNumber(),
                    page.getSize(),
                    page.getNumberOfElements(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast(),
                    page.isEmpty()
            );
        }
    }
}

