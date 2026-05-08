package com.moassam.user.application.dto;

import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.Post;

import java.time.LocalDateTime;

public record MyFreePostResponse(
        Long postId,
        String title,
        HeadTag headTag,
        long viewCount,
        LocalDateTime createdAt
) {
    public static MyFreePostResponse from(Post post) {
        return new MyFreePostResponse(
                post.getId(),
                post.getTitle(),
                post.getHeadTag(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}