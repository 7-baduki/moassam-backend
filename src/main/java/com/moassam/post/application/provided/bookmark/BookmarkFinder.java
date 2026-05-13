package com.moassam.post.application.provided.bookmark;

import com.moassam.post.domain.post.Post;
import org.springframework.data.domain.Page;

public interface BookmarkFinder {
    long countByUserId(Long userId);

    Page<Post> getBookmarkedPosts(Long userId, int page, int size);
}
