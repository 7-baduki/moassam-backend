package com.moassam.support.post;

import com.moassam.post.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class PostFixture {

    public static Post createMoabangPost(Long postId) {
        Post post = Post.create(
                1L,
                "자료 공유 합니다",
                "가나다라마바사아자차카타파하",
                Category.MOABANG,
                Age.AGE_3,
                ResourceType.JOURNAL,
                null
        );

        ReflectionTestUtils.setField(post, "id", postId);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());

        return post;
    }

    public static Post createFreePost(Long postId) {
        Post post = Post.create(
                1L,
                "질문있습니다.",
                "가나다라마바사아자차카타파하",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        ReflectionTestUtils.setField(post, "id", postId);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());

        return post;
    }
}
