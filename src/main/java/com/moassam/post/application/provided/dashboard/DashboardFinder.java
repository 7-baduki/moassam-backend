package com.moassam.post.application.provided.dashboard;

import com.moassam.post.domain.post.*;
import com.moassam.post.domain.dashboard.FreeDashboardDetail;
import com.moassam.post.domain.dashboard.MoabangDashboardDetail;
import org.springframework.data.domain.Page;

public interface DashboardFinder {
    Page<MoabangDashboardDetail> getMoabangDashboard(PostAge postAge, ResourceType resourceType, int page, int size);

    Page<FreeDashboardDetail> getFreeDashboard(HeadTag headTag, int page, int size);

    Page<MoabangDashboardDetail> searchMoabang(String keyword, int page, int size);
    
    Page<FreeDashboardDetail> searchFree(String keyword, int page, int size);
}
