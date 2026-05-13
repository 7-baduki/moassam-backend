package com.moassam.post.application;

import com.moassam.post.application.provided.bookmark.BookmarkFinder;
import com.moassam.post.application.provided.bookmark.BookmarkRegister;
import com.moassam.post.application.required.BookmarkRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.bookmark.PostBookmark;
import com.moassam.post.domain.post.Post;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class BookmarkService implements BookmarkRegister, BookmarkFinder {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    @Override
    public void bookmark(Long userId, Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        if (bookmarkRepository.existsByPostIdAndUserId(postId, userId)) {
            return;
        }

        bookmarkRepository.save(PostBookmark.create(userId, postId));
    }

    @Override
    public void unbookmark(Long userId, Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        bookmarkRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(bookmarkRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getBookmarkedPosts(Long userId, int page, int size) {
        return bookmarkRepository.findBookmarkedPostsByUserId(
                userId,
                PageRequest.of(page, size)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return bookmarkRepository.countByUserId(userId);
    }
}
