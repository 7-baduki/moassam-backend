package com.moassam.shared.web;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {

    public static <T> PageResponse<T> of(List<T> data, int page, int size, long totalElements) {
        int totalPages = (int) ((totalElements + size - 1) / size);
        boolean hasNext = (long) (page + 1) * size < totalElements;

        return new PageResponse<>(data, page, size, totalElements, totalPages, hasNext);
    }
}
