package com.moassam.post.domain;

import com.moassam.post.domain.post.*;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PostTest {

    @Test
    void creteFreePost() {
        Post post = Post.create(
                1L,
                "질문 있습니다.",
                "게시글 내용",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        assertThat(post.getUserId()).isEqualTo(1L);
        assertThat(post.getCategory()).isEqualTo(Category.FREE);
        assertThat(post.getHeadTag()).isEqualTo(HeadTag.QUESTION);
        assertThat(post.getPostAge()).isNull();
        assertThat(post.getResourceType()).isNull();
    }

    @Test
    void freePostEssentialCategory_HeadTag() {
        assertThatThrownBy(() -> Post.create(
                1L,
                "제목",
                "내용",
                Category.FREE,
                null,
                null,
                null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_INVALID_CATEGORY_OPTION);
    }

    @Test
    void freePostInvalidCategory_Age_ResourceType() {
        assertThatThrownBy(() -> Post.create(
                1L,
                "제목",
                "내용",
                Category.FREE,
                PostAge.AGE_3,
                ResourceType.JOURNAL,
                HeadTag.QUESTION
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_INVALID_CATEGORY_OPTION);
    }

    @Test
    void createMoabangPost() {
        Post post = Post.create(
                1L,
                "자료 공유합니다.",
                "게시글 내용",
                Category.MOABANG,
                PostAge.AGE_3,
                ResourceType.JOURNAL,
                null
        );

        assertThat(post.getCategory()).isEqualTo(Category.MOABANG);
        assertThat(post.getPostAge()).isEqualTo(PostAge.AGE_3);
        assertThat(post.getResourceType()).isEqualTo(ResourceType.JOURNAL);
        assertThat(post.getHeadTag()).isNull();
    }

    @Test
    void moabangPostEssentialCategory_Age_ResourceType() {
        assertThatThrownBy(() -> Post.create(
                1L,
                "제목",
                "내용",
                Category.MOABANG,
                null,
                null,
                null
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_INVALID_CATEGORY_OPTION);
    }

    @Test
    void moabangPostInvalidCategory_HeadTag() {
        assertThatThrownBy(() -> Post.create(
                1L,
                "제목",
                "내용",
                Category.MOABANG,
                PostAge.AGE_3,
                ResourceType.JOURNAL,
                HeadTag.QUESTION
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(PostErrorCode.POST_INVALID_CATEGORY_OPTION);
    }

    @Test
    void updatePost() {
        Post post = Post.create(
                1L,
                "기존 제목",
                "기존 내용",
                Category.FREE,
                null,
                null,
                HeadTag.QUESTION
        );

        post.update(
                "수정 제목",
                "수정 내용",
                Category.MOABANG,
                PostAge.AGE_3,
                ResourceType.JOURNAL,
                null
        );

        assertThat(post.getTitle()).isEqualTo("수정 제목");
        assertThat(post.getContent()).isEqualTo("수정 내용");
        assertThat(post.getCategory()).isEqualTo(Category.MOABANG);
        assertThat(post.getPostAge()).isEqualTo(PostAge.AGE_3);
        assertThat(post.getResourceType()).isEqualTo(ResourceType.JOURNAL);
        assertThat(post.getHeadTag()).isNull();
    }
}
