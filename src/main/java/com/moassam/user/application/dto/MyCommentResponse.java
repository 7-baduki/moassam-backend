package com.moassam.user.application.dto;

import com.moassam.post.domain.comment.Comment;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long commentId,
        String content,
        String postTitle,
        LocalDateTime createdAt
) {
    public static MyCommentResponse of(Comment comment, String postTitle) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getContent(),
                postTitle,
                comment.getCreatedAt()
        );
    }
}