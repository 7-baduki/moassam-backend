package com.moassam.admin.adapter.dto;

import com.moassam.shared.web.PageResponse;

public record AdminUserOverviewResponse(
        long totalUserCount,
        long periodUserCount,
        PageResponse<AdminUserResponse> users
) {
}
