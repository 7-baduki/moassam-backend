package com.moassam.post.domain.post;

import java.util.List;

public record PostDetail(
        Post post,
        String authorNickName,
        List<PostFile> files,
        boolean isLiked,
        boolean bookmarked
) {
}
