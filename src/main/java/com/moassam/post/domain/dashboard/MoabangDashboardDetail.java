package com.moassam.post.domain.dashboard;

import com.moassam.post.domain.post.PostAge;
import com.moassam.post.domain.post.ResourceType;

import java.time.LocalDateTime;

public record MoabangDashboardDetail(
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
}
