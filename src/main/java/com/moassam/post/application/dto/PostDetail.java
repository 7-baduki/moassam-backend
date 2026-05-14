package com.moassam.post.application.dto;

import com.moassam.post.domain.post.Post;
import com.moassam.post.domain.post.PostFile;

import java.util.List;

public record PostDetail(
        Post post,
        String authorNickName,
        List<PostFile> files,
        List<CommentDetail> comments,
        boolean isLiked,
        boolean bookmarked,
        boolean isMine
) {
}
