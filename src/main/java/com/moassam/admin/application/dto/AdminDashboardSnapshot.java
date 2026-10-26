package com.moassam.admin.application.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public record AdminDashboardSnapshot(
        LocalDateTime asOf,
        LocalDateTime todayStart,
        int periodDays,
        DashboardPeriod currentPeriod,
        DashboardPeriod comparisonPeriod,
        Today today,
        Cards cards,
        Dau dau,
        FeatureUsage featureUsage,
        Regeneration regeneration,
        AiUsage aiUsage,
        List<RecentPost> recentPosts,
        DataQuality dataQuality
) {
    public record Today(long signups, long activeUsers, long observationsCreated) {
    }

    public record Cards(long signups, Double signupChangePercent,
                        Double observationWritingRatePercent, long withdrawals,
                        long unusedWithdrawals, Availability lifecycleAvailability) {
    }

    public record Dau(List<DailyPoint> current, List<DailyPoint> comparison,
                      long uniqueActiveUsers, Double sevenDayRevisitRatePercent,
                      Availability availability) {
    }

    public record DailyPoint(LocalDate date, Long count) {
    }

    public record FeatureUsage(UsageSegment observationOnly,
                               UsageSegment observationAndCommunity,
                               UsageSegment communityOnly,
                               UsageSegment browseOnly,
                               Availability availability) {
    }

    public record UsageSegment(long count, Double percent) {
    }

    public record Regeneration(long created, long regenerated, Double ratePercent,
                               RegenerationWarning warning, Availability availability) {
    }

    public record RegenerationWarning(Double yesterdayRatePercent,
                                      Double previousSevenDayRatePercent,
                                      boolean warning) {
    }

    public record AiUsage(long calls, long successes, long failures,
                          long inputTokens, long outputTokens, long totalTokens,
                          Double averageDurationMs, Availability availability) {
    }

    public record RecentPost(long postId, String category, String title,
                             long commentCount, LocalDateTime createdAt) {
    }

    public record Availability(AvailabilityStatus status, LocalDate coveredFrom,
                               LocalDate coveredTo, boolean degraded) {
    }

    public record DataQuality(boolean degraded) {
    }

    public enum AvailabilityStatus {
        AVAILABLE,
        PARTIAL,
        COLLECTING
    }
}
