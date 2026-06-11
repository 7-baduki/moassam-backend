package com.moassam.post.application.required;

import com.moassam.post.domain.postlike.PostLike;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends Repository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    PostLike save(PostLike postLike);

    void delete(PostLike postLike);

    @Query("""
                select distinct l.postId
                from PostLike l
                where l.userId = :userId
            """)
    List<Long> findPostIdsByUserId(Long userId);

    @Modifying
    @Query(
            value = """
                    INSERT INTO post_likes (post_id, user_id, created_at, updated_at)
                    VALUES (:postId, :userId, NOW(), NOW())
                    ON CONFLICT (post_id, user_id) DO NOTHING
                    """,
            nativeQuery = true
    )
    int insertIgnore(Long postId, Long userId);

    @Modifying
    @Query(
            value = """
                    DELETE FROM post_likes
                    WHERE post_id = :postId
                      AND user_id = :userId
                    """,
            nativeQuery = true
    )
    int deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteByUserId(Long userId);
}
