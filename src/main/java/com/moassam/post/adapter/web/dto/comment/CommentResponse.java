package com.moassam.post.adapter.web.dto.comment;

import com.moassam.post.domain.comment.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        String authorNickname,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
