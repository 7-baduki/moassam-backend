package com.moassam.user.adapter.web.dto;

import com.moassam.post.domain.post.Post;

import java.time.LocalDateTime;

public record MyPostResponse(
        Long postId,
        String title,
        long viewCount,
        LocalDateTime createdAt
) {
    public static MyPostResponse from(Post post) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}