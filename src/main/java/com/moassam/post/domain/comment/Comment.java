package com.moassam.post.domain.comment;

import com.moassam.post.exception.CommentErrorCode;
import com.moassam.shared.domain.BaseEntity;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    private Long id;
    private Long postId;
    private Long userId;
    private String nickname;
    private String content;

    public static Comment create(
            Long postId,
            User user,
            String content
    ) {
        validateContent(content);

        Comment comment = new Comment();

        comment.postId = postId;
        comment.userId = user.getId();
        comment.nickname = user.getNickname();
        comment.content = content;

        return comment;
    }

    public void update(String content) {
        validateContent(content);
        this.content = content;
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(CommentErrorCode.COMMENT_CONTENT_REQUIRED);
        }
    }
}
