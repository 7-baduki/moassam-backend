package com.moassam.post.application.provided.bookmark;


public interface BookmarkRegister {

    void bookmark(Long userId, Long postId);

    void unbookmark(Long userId, Long postId);
}
