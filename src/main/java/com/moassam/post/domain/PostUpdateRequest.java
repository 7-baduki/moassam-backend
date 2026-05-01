package com.moassam.post.domain;

import java.util.List;

public record PostUpdateRequest(
        Category category,
        Age age,
        ResourceType resourceType,
        HeadTag headTag,
        String title,
        String content,
        List<Long> deleteFileIds
) {
}
