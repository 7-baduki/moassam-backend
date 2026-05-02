package com.moassam.post.application.provided.comment;

public interface CommentUpdater {
    Long updateComment(Long userId, Long postId, Long commentId, String content);
}
