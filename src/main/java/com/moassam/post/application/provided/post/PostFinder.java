package com.moassam.post.application.provided.post;

import com.moassam.post.application.dto.PostDetail;

public interface PostFinder {
    PostDetail getPost(Long userId, Long postId);
}
