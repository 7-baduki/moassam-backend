package com.moassam.post.application;

import com.moassam.post.application.required.CommentRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.comment.Comment;
import com.moassam.post.domain.post.Category;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.Post;
import com.moassam.post.exception.CommentErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.support.CommentFixture;
import com.moassam.support.UserFixture;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class CommentServiceTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final CommentService commentService =
            new CommentService(commentRepository, postRepository, userRepository);

    @Test
    void createComment() {
        User user = UserFixture.createWithNickname("나미리선생님");
        ReflectionTestUtils.setField(user, "id", 1L);

        Post post = Post.create(1L, "제목", "내용", Category.FREE, null, null, HeadTag.QUESTION);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class)))
                .willAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    ReflectionTestUtils.setField(comment, "id", 100L);
                    return comment;
                });

        Long commentId = commentService.createComment(1L, 10L, "댓글 내용");

        assertThat(commentId).isEqualTo(100L);
        assertThat(post.getCommentCount()).isEqualTo(1);
        then(commentRepository).should().save(any(Comment.class));
    }

    @Test
    void getComment() {
        Comment comment = CommentFixture.create(100L, 10L, 1L);
        given(commentRepository.findById(100L)).willReturn(Optional.of(comment));

        Comment found = commentService.getComment(10L, 100L);

        assertThat(found).isEqualTo(comment);
    }

    @Test
    void updateComment() {
        Comment comment = CommentFixture.create(100L, 10L, 1L);
        given(commentRepository.findById(100L)).willReturn(Optional.of(comment));

        Long commentId = commentService.updateComment(1L, 10L, 100L, "수정 댓글");

        assertThat(commentId).isEqualTo(100L);
        assertThat(comment.getContent()).isEqualTo("수정 댓글");
    }

    @Test
    void updateComment_notOwner() {
        Comment comment = CommentFixture.create(100L, 10L, 1L);
        given(commentRepository.findById(100L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(2L, 10L, 100L, "수정 댓글"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommentErrorCode.COMMENT_FORBIDDEN);
    }

    @Test
    void deleteComment() {
        Comment comment = CommentFixture.create(100L, 10L, 1L);
        Post post = Post.create(1L, "제목", "내용", Category.FREE, null, null, HeadTag.QUESTION);
        post.increaseCommentCount();

        given(commentRepository.findById(100L)).willReturn(Optional.of(comment));
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        commentService.deleteComment(1L, 10L, 100L);

        assertThat(post.getCommentCount()).isZero();
        then(commentRepository).should().delete(comment);
    }}
