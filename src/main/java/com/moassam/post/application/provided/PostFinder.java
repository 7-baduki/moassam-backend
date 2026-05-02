package com.moassam.post.application.provided;

import com.moassam.post.domain.PostDetail;

public interface PostFinder {
    PostDetail getPost(Long userId, Long postId);
}
