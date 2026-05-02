package com.moassam.post.application.provided.comment;

public interface CommentCreator {
    Long createComment(Long userId, Long postId, String content);
}
