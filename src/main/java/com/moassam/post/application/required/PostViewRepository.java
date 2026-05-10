package com.moassam.post.application.required;

import com.moassam.post.domain.postview.PostView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface PostViewRepository extends Repository<PostView, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO post_views (post_id, user_id, created_at, updated_at)
        VALUES (:postId, :userId, now(), now())
        ON CONFLICT (post_id, user_id) DO NOTHING
        """, nativeQuery = true)
    int insertIgnore(@Param("postId") Long postId, @Param("userId") Long userId);
}
