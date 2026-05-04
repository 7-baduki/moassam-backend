package com.moassam.support;

import com.moassam.post.domain.comment.Comment;
import com.moassam.user.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class CommentFixture {

    public static Comment create(Long id, Long postId, Long userId) {
        User user = UserFixture.createWithNickname("나미리선생님");
        ReflectionTestUtils.setField(user, "id", userId);

        Comment comment = Comment.create(postId, user, "댓글 내용");
        ReflectionTestUtils.setField(comment, "id", id);
        ReflectionTestUtils.setField(comment, "createdAt", LocalDateTime.now());

        return comment;
    }
}
