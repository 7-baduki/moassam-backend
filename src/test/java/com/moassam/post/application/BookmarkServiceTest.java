package com.moassam.post.application;

import com.moassam.post.application.required.BookmarkRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.bookmark.PostBookmark;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.Post;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class BookmarkServiceTest {

    private final BookmarkRepository bookmarkRepository = mock(BookmarkRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);

    private final BookmarkService bookmarkService =
            new BookmarkService(bookmarkRepository, postRepository);

    @Test
    void bookmark() {
        Post post = Post.create(99L, "title", "content", Category.FREE, null, null, HeadTag.QUESTION);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 1L)).willReturn(false);

        bookmarkService.bookmark(1L, 10L);

        ArgumentCaptor<PostBookmark> captor = ArgumentCaptor.forClass(PostBookmark.class);
        then(bookmarkRepository).should().save(captor.capture());

        PostBookmark savedBookmark = captor.getValue();
        assertThat(savedBookmark.getUserId()).isEqualTo(1L);
        assertThat(savedBookmark.getPostId()).isEqualTo(10L);
    }

    @Test
    void bookmark_alreadyBookmarked() {
        Post post = Post.create(99L, "title", "content", Category.FREE, null, null, HeadTag.QUESTION);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(bookmarkRepository.existsByPostIdAndUserId(10L, 1L)).willReturn(true);

        bookmarkService.bookmark(1L, 10L);

        then(bookmarkRepository).should(never()).save(any(PostBookmark.class));
    }

    @Test
    void bookmark_postNotFound() {
        given(postRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookmarkService.bookmark(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    void unbookmark() {
        Post post = Post.create(99L, "title", "content", Category.FREE, null, null, HeadTag.QUESTION);
        PostBookmark bookmark = PostBookmark.create(1L, 10L);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(bookmarkRepository.findByPostIdAndUserId(10L, 1L)).willReturn(Optional.of(bookmark));

        bookmarkService.unbookmark(1L, 10L);

        then(bookmarkRepository).should().delete(bookmark);
    }

    @Test
    void unbookmark_notBookmarked() {
        Post post = Post.create(99L, "title", "content", Category.FREE, null, null, HeadTag.QUESTION);

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(bookmarkRepository.findByPostIdAndUserId(10L, 1L)).willReturn(Optional.empty());

        bookmarkService.unbookmark(1L, 10L);

        then(bookmarkRepository).should(never()).delete(any(PostBookmark.class));
    }

    @Test
    void unbookmark_postNotFound() {
        given(postRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookmarkService.unbookmark(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    void getBookmarkedPosts() {
        Post post = Post.create(
                99L,
                "title",
                "content",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        PageRequest pageable = PageRequest.of(0, 10);

        given(bookmarkRepository.findBookmarkedPostsByUserId(1L, pageable))
                .willReturn(new PageImpl<>(List.of(post), pageable, 1));

        Page<Post> result = bookmarkService.getBookmarkedPosts(1L, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("title");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void countByUserId() {
        given(bookmarkRepository.countByUserId(1L)).willReturn(3L);

        long result = bookmarkService.countByUserId(1L);

        assertThat(result).isEqualTo(3L);
    }
}