package com.moassam.post.domain.post;

import java.util.List;

public record PostUpdateRequest(
        Category category,
        PostAge postAge,
        ResourceType resourceType,
        HeadTag headTag,
        String title,
        String content,
        List<Long> deleteFileIds
) {
}
