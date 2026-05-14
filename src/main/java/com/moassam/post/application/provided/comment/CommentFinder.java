package com.moassam.post.application.provided.comment;

import com.moassam.post.application.dto.CommentDetail;

public interface CommentFinder {
    CommentDetail getComment(Long userId, Long postId, Long commentId);
}
