package com.moassam.post.application.required;

import com.moassam.post.domain.bookmark.PostBookmark;
import com.moassam.post.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query(
            value = """
            INSERT INTO post_bookmarks (post_id, user_id, created_at, updated_at)
            VALUES (:postId, :userId, NOW(), NOW())
            ON CONFLICT (post_id, user_id) DO NOTHING
            """,
            nativeQuery = true
    )
    int insertIgnore(Long postId, Long userId);

    @Modifying
    @Query(
            value = """
                    DELETE FROM post_bookmarks
                    WHERE post_id = :postId
                      AND user_id = :userId
                    """,
            nativeQuery = true
    )
    int deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteByUserId(Long userId);
}
