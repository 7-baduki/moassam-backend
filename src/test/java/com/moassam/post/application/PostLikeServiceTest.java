package com.moassam.post.application;

import com.moassam.post.application.required.PostLikeRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.Post;
import com.moassam.post.domain.postlike.PostLike;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

public class PostLikeServiceTest {

    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);

    private final PostLikeService postLikeService =
            new PostLikeService(postLikeRepository, postRepository);

    @Test
    void like() {
        Post post = Post.create(99L, "제목", "내용", Category.FREE, null, null, HeadTag.QUESTION);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUserId(10L, 1L)).willReturn(false);

        postLikeService.like(1L, 10L);

        assertThat(post.getLikeCount()).isEqualTo(1);

        ArgumentCaptor<PostLike> captor = ArgumentCaptor.forClass(PostLike.class);
        then(postLikeRepository).should().save(captor.capture());

        PostLike savedPostLike = captor.getValue();
        assertThat(savedPostLike.getUserId()).isEqualTo(1L);
        assertThat(savedPostLike.getPostId()).isEqualTo(10L);
    }

    @Test
    void like_alreadyLiked() {
        Post post = Post.create(99L, "제목", "내용", Category.FREE, null, null, HeadTag.QUESTION);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUserId(10L, 1L)).willReturn(true);

        postLikeService.like(1L, 10L);

        assertThat(post.getLikeCount()).isZero();
        then(postLikeRepository).should(never()).save(any(PostLike.class));
    }

    @Test
    void like_postNotFound() {
        given(postRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postLikeService.like(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    void unlike() {
        Post post = Post.create(99L, "제목", "내용", Category.FREE, null, null, HeadTag.QUESTION);
        post.increaseLikeCount();

        PostLike postLike = PostLike.create(1L, 10L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostIdAndUserId(10L, 1L)).willReturn(Optional.of(postLike));

        postLikeService.unlike(1L, 10L);

        assertThat(post.getLikeCount()).isZero();
        then(postLikeRepository).should().delete(postLike);
    }

    @Test
    void unlike_notLiked() {
        Post post = Post.create(99L, "제목", "내용", Category.FREE, null, null, HeadTag.QUESTION);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostIdAndUserId(10L, 1L)).willReturn(Optional.empty());

        postLikeService.unlike(1L, 10L);

        assertThat(post.getLikeCount()).isZero();
        then(postLikeRepository).should(never()).delete(any(PostLike.class));
    }

    @Test
    void unlike_postNotFound() {
        given(postRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postLikeService.unlike(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }
}
