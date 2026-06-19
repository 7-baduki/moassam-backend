package com.moassam.admin.adapter.dto;

import com.moassam.post.domain.post.Category;

import java.time.LocalDateTime;

public record AdminPostResponse(
        Long postId,
        String title,
        String author,
        Category category,
        LocalDateTime createdAt,
        long viewCount
) {
}
