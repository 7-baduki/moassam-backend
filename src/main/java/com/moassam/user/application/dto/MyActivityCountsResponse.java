package com.moassam.user.application.dto;

public record MyActivityCountsResponse(
        long observationCount,
        long bookmarkedPostCount
) {
    public static MyActivityCountsResponse from(long observationCount, long bookmarkedPostCount) {
        return new MyActivityCountsResponse(observationCount, bookmarkedPostCount);
    }
}
