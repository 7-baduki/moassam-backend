package com.moassam.post.domain.postlike;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseEntity {

    private Long id;
    private Long postId;
    private Long userId;

    public static PostLike create(Long userId, Long postId) {
        PostLike postLike = new PostLike();

        postLike.userId = userId;
        postLike.postId = postId;

        return postLike;
    }
}
