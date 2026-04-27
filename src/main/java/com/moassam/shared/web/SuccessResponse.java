package com.moassam.shared.web;

public record SuccessResponse<T>(T data) {

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }
}
