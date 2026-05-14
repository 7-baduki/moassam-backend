package com.moassam.post.application.dto;

import com.moassam.post.domain.comment.Comment;

public record CommentDetail(
        Comment comment,
        boolean isMine
) {
}
