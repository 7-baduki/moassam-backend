package com.moassam.admin.application.dto;

import org.springframework.data.domain.Page;

public record AdminUserSearchResult(
        long totalUserCount,
        Page<AdminUserSummary> users
) {
    public long periodUserCount() {
        return users.getTotalElements();
    }
}
