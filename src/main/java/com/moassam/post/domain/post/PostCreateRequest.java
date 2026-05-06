package com.moassam.post.domain.post;

public record PostCreateRequest(
        Category category,
        PostAge postAge,
        ResourceType resourceType,
        HeadTag headTag,
        String title,
        String content
) {
}
