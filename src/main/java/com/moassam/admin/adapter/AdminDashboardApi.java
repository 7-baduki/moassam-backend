package com.moassam.admin.adapter;

import com.moassam.admin.adapter.annotation.RequireSuperAdmin;
import com.moassam.admin.adapter.dto.AdminDashboardResponse;
import com.moassam.admin.application.AdminDashboardService;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/dashboard")
@RestController
@RequireSuperAdmin
public class AdminDashboardApi {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public SuccessResponse<AdminDashboardResponse> getDashboard(
            @RequestParam(defaultValue = "30") int periodDays
    ) {
        return SuccessResponse.of(AdminDashboardResponse.from(
                adminDashboardService.getDashboard(periodDays)
        ));
    }
}
