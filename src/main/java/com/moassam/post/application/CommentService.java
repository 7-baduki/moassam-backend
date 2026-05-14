package com.moassam.post.application;

import com.moassam.post.application.dto.CommentDetail;
import com.moassam.post.application.provided.comment.CommentCreator;
import com.moassam.post.application.provided.comment.CommentDeleter;
import com.moassam.post.application.provided.comment.CommentFinder;
import com.moassam.post.application.provided.comment.CommentUpdater;
import com.moassam.post.application.required.CommentRepository;
import com.moassam.post.application.required.PostRepository;
import com.moassam.post.domain.comment.Comment;
import com.moassam.post.domain.post.Post;
import com.moassam.post.exception.CommentErrorCode;
import com.moassam.post.exception.PostErrorCode;
import com.moassam.shared.exception.BusinessException;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import com.moassam.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService implements CommentCreator, CommentFinder, CommentUpdater, CommentDeleter {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createComment(Long userId, Long postId, String content) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        post.increaseCommentCount();

        Comment comment = Comment.create(postId, user, content);
        Comment savedComment = commentRepository.save(comment);

        return savedComment.getId();
    }

    @Override
    public CommentDetail getComment(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));

        validateCommentBelongsToPost(comment, postId);

        return new CommentDetail(
                comment,
                comment.isOwner(userId)
        );
    }

    @Override
    @Transactional
    public Long updateComment(Long userId, Long postId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));

        validateCommentBelongsToPost(comment, postId);
        validateCommentAuthor(comment, userId);

        comment.update(content);

        return comment.getId();
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));

        validateCommentBelongsToPost(comment, postId);
        validateCommentAuthor(comment, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorCode.POST_NOT_FOUND));

        post.decreaseCommentCount();

        commentRepository.delete(comment);
    }

    private void validateCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPostId().equals(postId)) {
            throw new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND);
        }
    }

    private void validateCommentAuthor(Comment comment, Long userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(CommentErrorCode.COMMENT_FORBIDDEN);
        }
    }
}
