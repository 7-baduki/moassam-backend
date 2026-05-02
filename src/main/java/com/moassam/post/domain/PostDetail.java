package com.moassam.post.domain;

import java.util.List;

public record PostDetail(
        Post post,
        String authorNickName,
        List<PostFile> files,
        boolean bookmarked
) {
}
