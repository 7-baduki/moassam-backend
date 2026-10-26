package com.moassam.admin.adapter.dto;

import com.moassam.admin.application.dto.AdminDashboardSnapshot;
import com.moassam.admin.application.dto.DashboardPeriod;

import java.time.LocalDateTime;
import java.util.List;

public record AdminDashboardResponse(
        LocalDateTime asOf,
        LocalDateTime todayStart,
        int periodDays,
        PeriodResponse currentPeriod,
        PeriodResponse comparisonPeriod,
        AdminDashboardSnapshot.Today today,
        AdminDashboardSnapshot.Cards cards,
        AdminDashboardSnapshot.Dau dau,
        AdminDashboardSnapshot.FeatureUsage featureUsage,
        AdminDashboardSnapshot.Regeneration regeneration,
        AdminDashboardSnapshot.AiUsage aiUsage,
        List<AdminDashboardSnapshot.RecentPost> recentPosts,
        AdminDashboardSnapshot.DataQuality dataQuality
) {
    public static AdminDashboardResponse from(AdminDashboardSnapshot snapshot) {
        return new AdminDashboardResponse(
                snapshot.asOf(),
                snapshot.todayStart(),
                snapshot.periodDays(),
                PeriodResponse.from(snapshot.currentPeriod()),
                PeriodResponse.from(snapshot.comparisonPeriod()),
                snapshot.today(),
                snapshot.cards(),
                snapshot.dau(),
                snapshot.featureUsage(),
                snapshot.regeneration(),
                snapshot.aiUsage(),
                snapshot.recentPosts(),
                snapshot.dataQuality()
        );
    }

    public record PeriodResponse(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        private static PeriodResponse from(DashboardPeriod period) {
            return new PeriodResponse(period.startInclusive(), period.endExclusive());
        }
    }
}
