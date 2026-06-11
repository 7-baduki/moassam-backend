package com.moassam.post.application;

import com.moassam.post.application.provided.bookmark.BookmarkFinder;
import com.moassam.post.application.provided.bookmark.BookmarkRegister;
import com.moassam.post.application.required.BookmarkRepository;
import com.moassam.post.application.required.PostRepository;
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
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(PostErrorCode.POST_NOT_FOUND);
        }

        int inserted = bookmarkRepository.insertIgnore(postId, userId);

        if (inserted == 1) {
            postRepository.increaseBookmarkCount(postId);
        }
    }

    @Override
    public void unbookmark(Long userId, Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(PostErrorCode.POST_NOT_FOUND);
        }

        int deleted = bookmarkRepository.deleteByPostIdAndUserId(postId, userId);

        if (deleted == 1) {
            postRepository.decreaseBookmarkCount(postId);
        }
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
