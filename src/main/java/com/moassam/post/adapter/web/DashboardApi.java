package com.moassam.post.adapter.web;

import com.moassam.post.adapter.web.dto.dashboard.FreeDashboardResponse;
import com.moassam.post.adapter.web.dto.dashboard.MoabangDashboardResponse;
import com.moassam.post.application.provided.dashboard.DashboardFinder;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import com.moassam.post.domain.post.HeadTag;
import com.moassam.post.domain.post.PostAge;
import com.moassam.post.domain.post.ResourceType;
import com.moassam.shared.web.PageResponse;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class DashboardApi {

    private final DashboardFinder dashboardFinder;

    @GetMapping("/moabang")
    public SuccessResponse<PageResponse<MoabangDashboardResponse>> getMoabangDashboard(
            @RequestParam(required = false) PostAge postAge,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<MoabangDashboardDetail> moabangDashboard = dashboardFinder.getMoabangDashboard(postAge, resourceType, page, size);

        Page<MoabangDashboardResponse> responsePage = moabangDashboard.map(MoabangDashboardResponse::from);


        return SuccessResponse.of(
                PageResponse.of(
                        responsePage.getContent(),
                        responsePage.getNumber(),
                        responsePage.getSize(),
                        responsePage.getTotalElements()
                )
        );
    }

    @GetMapping("/free")
    public SuccessResponse<PageResponse<FreeDashboardResponse>> getFreeDashboard(
            @RequestParam(required = false) HeadTag headTag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<FreeDashboardDetail> freeDashboard = dashboardFinder.getFreeDashboard(headTag, page, size);

        Page<FreeDashboardResponse> responsePage = freeDashboard.map(FreeDashboardResponse::from);

        return SuccessResponse.of(
                PageResponse.of(
                        responsePage.getContent(),
                        responsePage.getNumber(),
                        responsePage.getSize(),
                        responsePage.getTotalElements()
                )
        );
    }

    @GetMapping("/moabang/search")
    public SuccessResponse<PageResponse<MoabangDashboardResponse>> searchMoabang(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<MoabangDashboardDetail> result = dashboardFinder.searchMoabang(keyword, page, size);

        Page<MoabangDashboardResponse> responsePage = result.map(MoabangDashboardResponse::from);

        return SuccessResponse.of(PageResponse.of(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements()
        ));
    }

    @GetMapping("/free/search")
    public SuccessResponse<PageResponse<FreeDashboardResponse>> searchFree(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<FreeDashboardDetail> result = dashboardFinder.searchFree(keyword, page, size);

        Page<FreeDashboardResponse> responsePage = result.map(FreeDashboardResponse::from);

        return SuccessResponse.of(PageResponse.of(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements()
        ));
    }
}
