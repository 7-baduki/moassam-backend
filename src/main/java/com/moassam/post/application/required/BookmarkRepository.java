package com.moassam.post.application.required;

import com.moassam.post.domain.bookmark.PostBookmark;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BookmarkRepository extends Repository<PostBookmark, Long> {

    Optional<PostBookmark> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    PostBookmark save(PostBookmark postBookmark);

    void delete(PostBookmark postBookmark);
}
