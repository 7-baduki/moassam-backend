package com.moassam.post.application.provided.comment;

public interface CommentDeleter {
    void deleteComment(Long userId, Long postId, Long commentId);
}
