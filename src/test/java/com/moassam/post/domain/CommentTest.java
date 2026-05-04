package com.moassam.post.domain;

import com.moassam.post.domain.comment.Comment;
import com.moassam.post.exception.CommentErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.support.UserFixture;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommentTest {

    @Test
    void createComment() {
        User user = UserFixture.createWithNickname("나미리선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        Comment comment = Comment.create(10L, user, "댓글 내용");

        assertThat(comment.getPostId()).isEqualTo(10L);
        assertThat(comment.getUserId()).isEqualTo(1L);
        assertThat(comment.getNickname()).isEqualTo("나미리선생님");
        assertThat(comment.getContent()).isEqualTo("댓글 내용");
    }

    @Test
    void updateComment() {
        User user = UserFixture.createWithNickname("작성자");
        ReflectionTestUtils.setField(user, "id", 1L);
        Comment comment = Comment.create(10L, user, "기존 댓글");

        comment.update("수정 댓글");

        assertThat(comment.getContent()).isEqualTo("수정 댓글");
    }

    @Test
    void fail_blankContent() {
        User user = UserFixture.createWithNickname("작성자");
        ReflectionTestUtils.setField(user, "id", 1L);

        assertThatThrownBy(() -> Comment.create(10L, user, " "))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommentErrorCode.COMMENT_CONTENT_REQUIRED);
    }
}
