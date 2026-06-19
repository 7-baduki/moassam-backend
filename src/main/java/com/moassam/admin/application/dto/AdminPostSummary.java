package com.moassam.admin.application.dto;

import com.moassam.post.domain.post.Category;

import java.time.LocalDateTime;

public record AdminPostSummary(
        Long postId,
        String title,
        String author,
        Category category,
        LocalDateTime createdAt,
        long viewCount
) {
}
