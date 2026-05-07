package com.moassam.post.adapter.web.dto.dashboard;

import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import com.moassam.post.domain.post.PostAge;
import com.moassam.post.domain.post.ResourceType;

import java.time.LocalDateTime;

public record MoabangDashboardResponse(
        Long postId,
        String title,
        String authorNickName,
        String thumbnailUrl,
        PostAge postAge,
        ResourceType resourceType,
        long viewCount,
        long likeCount,
        long commentCount,
        LocalDateTime createdAt
) {
    public static MoabangDashboardResponse from(MoabangDashboardDetail detail) {
        return new MoabangDashboardResponse(
                detail.postId(),
                detail.title(),
                detail.authorNickName(),
                detail.thumbnailUrl(),
                detail.postAge(),
                detail.resourceType(),
                detail.viewCount(),
                detail.likeCount(),
                detail.commentCount(),
                detail.createdAt()
        );
    }
}
