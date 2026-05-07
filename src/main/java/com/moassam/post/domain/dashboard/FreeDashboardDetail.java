package com.moassam.post.domain.dashboard;

import com.moassam.post.domain.post.HeadTag;

import java.time.LocalDateTime;

public record FreeDashboardDetail(
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
}
