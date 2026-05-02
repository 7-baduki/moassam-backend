package com.moassam.post.domain.post;

public record PostCreateRequest(
        Category category,
        Age age,
        ResourceType resourceType,
        HeadTag headTag,
        String title,
        String content
) {
}
