package com.moassam.post.adapter.web.dto.post;

import com.moassam.post.domain.post.*;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        Long authorId,
        String authorNickName,
        String title,
        Category category,
        Age age,
        ResourceType resourceType,
        HeadTag headTag,
        String content,
        List<PostFileResponse> files,
        List<PostFileResponse> editorFiles,
        long viewCount,
        long commentCount,
        long likeCount,
        boolean bookmarked,
        LocalDateTime createdAt
) {
    public static PostDetailResponse from(PostDetail postDetail) {
        Post post = postDetail.post();
        List<PostFile> postFiles = postDetail.files();

        return new PostDetailResponse(
                post.getId(),
                post.getUserId(),
                postDetail.authorNickName(),
                post.getTitle(),
                post.getCategory(),
                post.getAge(),
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
                post.getViewCount(),
                post.getCommentCount(),
                post.getLikeCount(),
                postDetail.bookmarked(),
                post.getCreatedAt()
        );
    }
}
