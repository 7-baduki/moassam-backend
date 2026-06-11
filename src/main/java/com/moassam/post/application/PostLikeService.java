package com.moassam.post.application;

import com.moassam.post.application.provided.postlike.PostLikeRegister;
import com.moassam.post.application.required.PostLikeRepository;
import com.moassam.post.application.required.PostRepository;
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
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(PostErrorCode.POST_NOT_FOUND);
        }

        int inserted = postLikeRepository.insertIgnore(postId, userId);

        if (inserted == 1) {
            postRepository.increaseLikeCount(postId);
        }
    }

    @Override
    public void unlike(Long userId, Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(PostErrorCode.POST_NOT_FOUND);
        }

        int deleted = postLikeRepository.deleteByPostIdAndUserId(postId, userId);

        if (deleted == 1) {
            postRepository.decreaseLikeCount(postId);
        }
    }
}
