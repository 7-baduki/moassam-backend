package com.moassam.post.domain.post;

import com.moassam.post.domain.comment.Comment;

import java.util.List;

public record PostDetail(
        Post post,
        String authorNickName,
        List<PostFile> files,
        List<Comment> comments,
        boolean isLiked,
        boolean bookmarked
) {
}
