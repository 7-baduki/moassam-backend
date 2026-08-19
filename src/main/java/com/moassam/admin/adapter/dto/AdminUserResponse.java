package com.moassam.admin.adapter.dto;

import com.moassam.user.domain.Provider;
import com.moassam.user.domain.Role;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long userId,
        String nickname,
        Provider provider,
        Role role,
        LocalDateTime createdAt,
        AdminUserStatus status
) {
}
