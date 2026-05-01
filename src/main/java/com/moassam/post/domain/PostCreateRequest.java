package com.moassam.post.domain;

public record PostCreateRequest(
        Category category,
        Age age,
        ResourceType resourceType,
        HeadTag headTag,
        String title,
        String content
) {
}
