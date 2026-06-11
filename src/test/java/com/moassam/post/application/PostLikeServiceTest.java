package com.moassam.post.application;

import com.moassam.post.application.required.PostLikeRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

public class PostLikeServiceTest {

    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);

    private final PostLikeService postLikeService =
            new PostLikeService(postLikeRepository, postRepository);

    @Test
    void like() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(postLikeRepository.insertIgnore(10L, 1L)).willReturn(1);

        postLikeService.like(1L, 10L);

        then(postLikeRepository).should().insertIgnore(10L, 1L);
        then(postRepository).should().increaseLikeCount(10L);
    }

    @Test
    void like_alreadyLiked() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(postLikeRepository.insertIgnore(10L, 1L)).willReturn(0);

        postLikeService.like(1L, 10L);

        then(postLikeRepository).should().insertIgnore(10L, 1L);
        then(postRepository).should(never()).increaseLikeCount(10L);
    }

    @Test
    void like_postNotFound() {
        given(postRepository.existsById(10L)).willReturn(false);

        assertThatThrownBy(() -> postLikeService.like(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);

        then(postLikeRepository).should(never()).insertIgnore(anyLong(), anyLong());
        then(postRepository).should(never()).increaseLikeCount(anyLong());
    }

    @Test
    void unlike() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(postLikeRepository.deleteByPostIdAndUserId(10L, 1L)).willReturn(1);

        postLikeService.unlike(1L, 10L);

        then(postLikeRepository).should().deleteByPostIdAndUserId(10L, 1L);
        then(postRepository).should().decreaseLikeCount(10L);
    }

    @Test
    void unlike_notLiked() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(postLikeRepository.deleteByPostIdAndUserId(10L, 1L)).willReturn(0);

        postLikeService.unlike(1L, 10L);

        then(postLikeRepository).should().deleteByPostIdAndUserId(10L, 1L);
        then(postRepository).should(never()).decreaseLikeCount(10L);
    }

    @Test
    void unlike_postNotFound() {
        given(postRepository.existsById(10L)).willReturn(false);

        assertThatThrownBy(() -> postLikeService.unlike(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);

        then(postLikeRepository).should(never()).deleteByPostIdAndUserId(anyLong(), anyLong());
        then(postRepository).should(never()).decreaseLikeCount(anyLong());
    }
}
