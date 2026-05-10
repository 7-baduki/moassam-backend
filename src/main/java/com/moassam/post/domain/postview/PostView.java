package com.moassam.post.domain.postview;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostView extends BaseEntity {

    private Long id;
    private Long postId;
    private Long userId;

    public static PostView create(Long postId, Long userId) {
        PostView postView = new PostView();

        postView.postId = postId;
        postView.userId = userId;

        return postView;
    }
}
