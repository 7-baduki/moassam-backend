package com.moassam.post.application.provided.post;

import com.moassam.post.domain.post.PostDetail;

public interface PostFinder {
    PostDetail getPost(Long userId, Long postId);

    Page<PostSearchResult> search(Category category, String keyword, Pageable pageable);
}
