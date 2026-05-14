package com.moassam.post.adapter.web.dto.comment;

import com.moassam.post.application.dto.CommentDetail;
import com.moassam.post.domain.comment.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long postId,
        String authorNickname,
        String content,
        boolean isMine,
        LocalDateTime createdAt
) {
    public static CommentResponse from(CommentDetail detail) {
        Comment comment = detail.comment();

        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getNickname(),
                comment.getContent(),
                detail.isMine(),
                comment.getCreatedAt()
        );
    }
}
