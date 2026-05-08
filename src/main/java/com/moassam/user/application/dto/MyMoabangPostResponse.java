package com.moassam.user.application.dto;

import com.moassam.post.domain.post.Post;
import com.moassam.post.domain.post.PostAge;
import com.moassam.post.domain.post.ResourceType;

import java.time.LocalDateTime;

public record MyMoabangPostResponse(
        Long postId,
        String title,
        PostAge postAge,
        ResourceType resourceType,
        long viewCount,
        LocalDateTime createdAt
) {
    public static MyMoabangPostResponse from(Post post) {
        return new MyMoabangPostResponse(
                post.getId(),
                post.getTitle(),
                post.getPostAge(),
                post.getResourceType(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}