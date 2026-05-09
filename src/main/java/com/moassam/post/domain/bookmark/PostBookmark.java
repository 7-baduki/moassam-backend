package com.moassam.post.domain.bookmark;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBookmark extends BaseEntity {

    private Long id;
    private Long postId;
    private Long userId;

    public static PostBookmark create(Long userId, Long postId) {
        PostBookmark postBookmark = new PostBookmark();

        postBookmark.userId = userId;
        postBookmark.postId = postId;

        return postBookmark;
    }
}
