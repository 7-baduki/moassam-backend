package com.moassam.post.application.required;

import com.moassam.post.domain.postlike.PostLike;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface PostLikeRepository extends Repository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    PostLike save(PostLike postLike);

    void delete(PostLike postLike);
}
