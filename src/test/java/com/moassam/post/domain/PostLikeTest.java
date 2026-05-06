package com.moassam.post.domain;

import com.moassam.post.domain.postlike.PostLike;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostLikeTest {

    @Test
    void create() {
        PostLike postLike = PostLike.create(1L, 10L);

        assertThat(postLike.getUserId()).isEqualTo(1L);
        assertThat(postLike.getPostId()).isEqualTo(10L);
    }
}