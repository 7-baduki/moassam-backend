package com.moassam.post.adapter.web.dto.dashboard;

import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.post.HeadTag;

import java.time.LocalDateTime;

public record FreeDashboardResponse(
        Long postId,
        String title,
        String authorNickName,
        String contentPreview,
        HeadTag headTag,
        long viewCount,
        long likeCount,
        long commentCount,
        LocalDateTime createdAt
) {
    public static FreeDashboardResponse from(FreeDashboardDetail detail) {
        return new FreeDashboardResponse(
                detail.postId(),
                detail.title(),
                detail.authorNickName(),
                detail.contentPreview(),
                detail.headTag(),
                detail.viewCount(),
                detail.likeCount(),
                detail.commentCount(),
                detail.createdAt()
        );
    }
}
