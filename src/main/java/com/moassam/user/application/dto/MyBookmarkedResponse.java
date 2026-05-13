package com.moassam.user.application.dto;

import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.Post;

import java.time.LocalDateTime;

public record MyBookmarkedResponse(
        Long postId,
        String title,
        Category category,
        long viewCount,
        LocalDateTime createdAt
) {
    public static MyBookmarkedResponse from(Post post) {
        return new MyBookmarkedResponse(
                post.getId(),
                post.getTitle(),
                post.getCategory(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
