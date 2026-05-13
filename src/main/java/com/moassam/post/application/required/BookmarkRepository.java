package com.moassam.post.application.required;

import com.moassam.post.domain.bookmark.PostBookmark;
import com.moassam.post.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BookmarkRepository extends Repository<PostBookmark, Long> {

    Optional<PostBookmark> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    PostBookmark save(PostBookmark postBookmark);

    void delete(PostBookmark postBookmark);

    @Query("""
        select p
        from PostBookmark b
        join Post p on p.id = b.postId
        where b.userId = :userId
        order by b.createdAt desc
    """)
    Page<Post> findBookmarkedPostsByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);
}
