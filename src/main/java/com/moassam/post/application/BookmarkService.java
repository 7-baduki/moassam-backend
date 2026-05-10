package com.moassam.post.application;

import com.moassam.post.application.provided.bookmark.BookmarkRegister;
import com.moassam.post.application.required.BookmarkRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.bookmark.PostBookmark;
import com.moassam.post.domain.post.Post;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class BookmarkService implements BookmarkRegister {

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
}
