package com.moassam.post.adapter.web.dto.post;

import com.moassam.post.adapter.web.dto.comment.CommentResponse;
import com.moassam.post.application.dto.CommentDetail;
import com.moassam.post.application.dto.PostDetail;
import com.moassam.post.domain.post.*;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        Long authorId,
        String authorNickName,
        String title,
        Category category,
        PostAge postAge,
        ResourceType resourceType,
        HeadTag headTag,
        String content,
        List<PostFileResponse> files,
        List<PostFileResponse> editorFiles,
        List<CommentResponse> comments,
        long viewCount,
        long commentCount,
        long likeCount,
        boolean isLiked,
        boolean bookmarked,
        boolean isMine,
        LocalDateTime createdAt
) {
    public static PostDetailResponse from(PostDetail postDetail) {
        Post post = postDetail.post();
        List<PostFile> postFiles = postDetail.files();
        List<CommentDetail> comments = postDetail.comments();

        return new PostDetailResponse(
                post.getId(),
                post.getUserId(),
                postDetail.authorNickName(),
                post.getTitle(),
                post.getCategory(),
                post.getPostAge(),
                post.getResourceType(),
                post.getHeadTag(),
                post.getContent(),
                postFiles.stream()
                        .filter(file -> !file.isEditorImage())
                        .map(PostFileResponse::from)
                        .toList(),
                postFiles.stream()
                        .filter(PostFile::isEditorImage)
                        .map(PostFileResponse::from)
                        .toList(),
                comments.stream()
                        .map(CommentResponse::from)
                        .toList(),
                post.getViewCount(),
                post.getCommentCount(),
                post.getLikeCount(),
                postDetail.isLiked(),
                postDetail.bookmarked(),
                postDetail.isMine(),
                post.getCreatedAt()
        );
    }
}
