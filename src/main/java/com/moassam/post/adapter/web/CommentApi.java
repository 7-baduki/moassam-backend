package com.moassam.post.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.post.adapter.web.dto.comment.CommentCreateResponse;
import com.moassam.post.adapter.web.dto.comment.CommentResponse;
import com.moassam.post.adapter.web.dto.comment.CommentUpdateResponse;
import com.moassam.post.application.dto.CommentDetail;
import com.moassam.post.application.provided.comment.CommentCreator;
import com.moassam.post.application.provided.comment.CommentDeleter;
import com.moassam.post.application.provided.comment.CommentFinder;
import com.moassam.post.application.provided.comment.CommentUpdater;
import com.moassam.post.domain.comment.CommentCreateRequest;
import com.moassam.post.domain.comment.CommentUpdateRequest;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentApi {

    private final CommentCreator commentCreator;
    private final CommentFinder commentFinder;
    private final CommentUpdater commentUpdater;
    private final CommentDeleter commentDeleter;

    @RequireAuth
    @PostMapping
    public SuccessResponse<CommentCreateResponse> createComment(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request
    ) {
        Long commentId = commentCreator.createComment(userId, postId, request.content());

        return SuccessResponse.of(new CommentCreateResponse(commentId));
    }

    @RequireAuth
    @GetMapping("/{commentId}")
    public SuccessResponse<CommentResponse> getComment(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        CommentDetail comment = commentFinder.getComment(userId, postId, commentId);

        return SuccessResponse.of(CommentResponse.from(comment));
    }

    @RequireAuth
    @PatchMapping("/{commentId}")
    public SuccessResponse<CommentUpdateResponse> updateComment(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        Long updatedCommentId = commentUpdater.updateComment(userId, postId, commentId, request.content());

        return SuccessResponse.of(new CommentUpdateResponse(updatedCommentId));
    }

    @RequireAuth
    @DeleteMapping("/{commentId}")
    public SuccessResponse<Void> deleteComment(
            @CurrentUserId Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentDeleter.deleteComment(userId, postId, commentId);

        return SuccessResponse.of(null);
    }
}
