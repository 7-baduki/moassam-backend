package com.moassam.user.application.dto;

import com.moassam.post.application.required.MyCommentProjection;
import com.moassam.post.domain.post.Category;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long commentId,
        Long postId,
        Category category,
        String content,
        String postTitle,
        LocalDateTime createdAt
) {
    public static MyCommentResponse from(MyCommentProjection projection) {
        return new MyCommentResponse(
                projection.commentId(),
                projection.postId(),
                projection.category(),
                projection.content(),
                projection.postTitle(),
                projection.createdAt()
        );
    }
}