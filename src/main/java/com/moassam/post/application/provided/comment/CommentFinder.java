package com.moassam.post.application.provided.comment;

import com.moassam.post.domain.comment.Comment;

public interface CommentFinder {
    Comment getComment(Long postId, Long commentId);
}
