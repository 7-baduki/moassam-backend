package com.moassam.admin.adapter;

import com.moassam.admin.adapter.annotation.RequireSuperAdmin;
import com.moassam.admin.adapter.dto.AdminUserOverviewResponse;
import com.moassam.admin.adapter.dto.AdminUserResponse;
import com.moassam.admin.adapter.dto.AdminUserStatus;
import com.moassam.admin.application.AdminUserService;
import com.moassam.shared.web.PageResponse;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
@RestController
@RequireSuperAdmin
public class AdminUserApi {

    private final AdminUserService adminUserService;

    @GetMapping
    public SuccessResponse<AdminUserOverviewResponse> getUsers(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var result = adminUserService.getUsers(startDate, endDate, page, size);
        var users = result.users().map(user -> new AdminUserResponse(
                user.userId(),
                user.nickname(),
                user.provider(),
                user.role(),
                user.createdAt(),
                user.deletedAt() == null ? AdminUserStatus.ACTIVE : AdminUserStatus.WITHDRAWN
        ));

        return SuccessResponse.of(new AdminUserOverviewResponse(
                result.totalUserCount(),
                result.periodUserCount(),
                PageResponse.of(users.getContent(), page, size, users.getTotalElements())
        ));
    }
}
