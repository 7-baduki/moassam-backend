package com.moassam.post.application.provided.postlike;

public interface PostLikeRegister {
    void like(Long userId, Long postId);

    void unlike(Long userId, Long postId);

}
