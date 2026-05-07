package com.moassam.post.application.provided.dashboard;

import com.moassam.post.domain.post.*;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import org.springframework.data.domain.Page;

public interface DashboardFinder {
    Page<MoabangDashboardDetail> getMoabangDashboard(Long userId, PostAge postAge, ResourceType resourceType, int page, int size);

    Page<FreeDashboardDetail> getFreeDashboard(Long userId, HeadTag headTag, int page, int size);
}
