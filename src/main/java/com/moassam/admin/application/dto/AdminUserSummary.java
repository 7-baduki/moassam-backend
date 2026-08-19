package com.moassam.admin.application.dto;

import com.moassam.user.domain.Provider;
import com.moassam.user.domain.Role;

import java.time.LocalDateTime;

public record AdminUserSummary(
        Long userId,
        String nickname,
        Provider provider,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}
