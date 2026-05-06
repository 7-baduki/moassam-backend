package com.moassam.post.application;

import com.moassam.post.application.provided.postlike.PostLikeRegister;
import com.moassam.post.application.required.PostLikeRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.post.Post;
import com.moassam.post.domain.postlike.PostLike;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@RequiredArgsConstructor
@Service
public class PostLikeService implements PostLikeRegister {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Override
    public void like(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            return;
        }

        post.increaseLikeCount();
        postLikeRepository.save(PostLike.create(userId, postId));
    }

    @Override
    public void unlike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(postLike -> {
                    post.decreaseLikeCount();
                    postLikeRepository.delete(postLike);
                });
    }
}