package com.moassam.post.application.required;

import com.moassam.post.domain.post.Category;

import java.time.LocalDateTime;

public record MyCommentProjection(
        Long commentId,
        Long postId,
        Category category,
        String content,
        String postTitle,
        LocalDateTime createdAt
) {
}
