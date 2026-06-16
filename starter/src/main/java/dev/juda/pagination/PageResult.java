package dev.juda.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        @JsonProperty("content")
        List<T> content,

        @JsonProperty("page")
        int page,

        @JsonProperty("size")
        int size,

        @JsonProperty("totalElements")
        long totalElements,

        @JsonProperty("totalPages")
        int totalPages,

        @JsonProperty("first")
        boolean first,

        @JsonProperty("last")
        boolean last
) {

    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(
                Collections.emptyList(),
                page,
                size,
                0,
                0,
                true,
                true
        );
    }

    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new PageResult<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }

    @SuppressWarnings("unchecked")
    public <U> PageResult<U> map(Function<? super T, ? extends U> mapper) {
        List<U> mapped = (List<U>) content.stream()
                .map(mapper)
                .toList();
        return new PageResult<>(mapped, page, size, totalElements, totalPages, first, last);
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }
}
