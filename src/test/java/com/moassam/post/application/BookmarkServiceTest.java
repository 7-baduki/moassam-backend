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
        given(postRepository.existsById(10L)).willReturn(true);
        given(bookmarkRepository.insertIgnore(10L, 1L)).willReturn(1);

        bookmarkService.bookmark(1L, 10L);

        then(bookmarkRepository).should().insertIgnore(10L, 1L);
        then(postRepository).should().increaseBookmarkCount(10L);
    }

    @Test
    void bookmark_alreadyBookmarked() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(bookmarkRepository.insertIgnore(10L, 1L)).willReturn(0);

        bookmarkService.bookmark(1L, 10L);

        then(bookmarkRepository).should().insertIgnore(10L, 1L);
        then(postRepository).should(never()).increaseBookmarkCount(10L);
    }

    @Test
    void bookmark_postNotFound() {
        given(postRepository.existsById(10L)).willReturn(false);

        assertThatThrownBy(() -> bookmarkService.bookmark(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);

        then(bookmarkRepository).should(never()).insertIgnore(anyLong(), anyLong());
        then(postRepository).should(never()).increaseBookmarkCount(anyLong());
    }

    @Test
    void unbookmark() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(bookmarkRepository.deleteByPostIdAndUserId(10L, 1L)).willReturn(1);

        bookmarkService.unbookmark(1L, 10L);

        then(bookmarkRepository).should().deleteByPostIdAndUserId(10L, 1L);
        then(postRepository).should().decreaseBookmarkCount(10L);
    }

    @Test
    void unbookmark_notBookmarked() {
        given(postRepository.existsById(10L)).willReturn(true);
        given(bookmarkRepository.deleteByPostIdAndUserId(10L, 1L)).willReturn(0);

        bookmarkService.unbookmark(1L, 10L);

        then(bookmarkRepository).should().deleteByPostIdAndUserId(10L, 1L);
        then(postRepository).should(never()).decreaseBookmarkCount(10L);
    }

    @Test
    void unbookmark_postNotFound() {
        given(postRepository.existsById(10L)).willReturn(false);

        assertThatThrownBy(() -> bookmarkService.unbookmark(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_NOT_FOUND);

        then(bookmarkRepository).should(never()).deleteByPostIdAndUserId(anyLong(), anyLong());
        then(postRepository).should(never()).decreaseBookmarkCount(anyLong());
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