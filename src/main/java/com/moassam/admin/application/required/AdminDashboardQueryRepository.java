package com.moassam.admin.application.required;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminDashboardQueryRepository {

    long countLifecycleEvents(String eventType, LocalDateTime startInclusive, LocalDateTime endExclusive);

    long countUnusedWithdrawals(LocalDateTime startInclusive, LocalDateTime endExclusive);

    long countSignupUsersWithObservation(LocalDateTime startInclusive, LocalDateTime endExclusive);

    long countActiveUsers(LocalDate startInclusive, LocalDate endExclusive);

    long countObservationCreated(LocalDate startInclusive, LocalDate endExclusive);

    List<DailyCount> findDailyActiveUsers(LocalDate startInclusive, LocalDate endExclusive);

    long countReturningUsers(LocalDate previousStartInclusive, LocalDate previousEndExclusive,
                             LocalDate recentStartInclusive, LocalDate recentEndExclusive);

    FeatureUsageCounts getFeatureUsage(LocalDate startInclusive, LocalDate endExclusive);

    GenerationCounts getGenerationCounts(LocalDate startInclusive, LocalDate endExclusive);

    AiUsageTotals getAiUsage(LocalDateTime startInclusive, LocalDateTime endExclusive);

    List<RecentPostSummary> findRecentPosts(int limit);

    CollectionState findCollectionState(String metricKey);

    record DailyCount(LocalDate date, long count) {
    }

    record FeatureUsageCounts(long observationOnly, long observationAndCommunity,
                              long communityOnly, long browseOnly) {
        public long total() {
            return observationOnly + observationAndCommunity + communityOnly + browseOnly;
        }
    }

    record GenerationCounts(long created, long regenerated) {
        public long total() {
            return created + regenerated;
        }
    }

    record AiUsageTotals(long calls, long successes, long failures, long inputTokens,
                         long outputTokens, long totalTokens, Double averageDurationMs) {
    }

    record RecentPostSummary(long postId, String category, String title,
                             long commentCount, LocalDateTime createdAt) {
    }

    record CollectionState(LocalDate availableFrom, boolean gapDetected) {
    }
}
