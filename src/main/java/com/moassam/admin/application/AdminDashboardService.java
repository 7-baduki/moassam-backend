package com.moassam.admin.application;

import com.moassam.admin.application.dto.AdminDashboardSnapshot;
import com.moassam.admin.application.dto.AdminDashboardSnapshot.Availability;
import com.moassam.admin.application.dto.AdminDashboardSnapshot.AvailabilityStatus;
import com.moassam.admin.application.dto.DashboardPeriod;
import com.moassam.admin.application.required.AdminDashboardQueryRepository;
import com.moassam.admin.application.required.AdminDashboardQueryRepository.AiUsageTotals;
import com.moassam.admin.application.required.AdminDashboardQueryRepository.CollectionState;
import com.moassam.admin.application.required.AdminDashboardQueryRepository.FeatureUsageCounts;
import com.moassam.admin.application.required.AdminDashboardQueryRepository.GenerationCounts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminDashboardService {

    private static final Set<Integer> SUPPORTED_PERIOD_DAYS = Set.of(7, 30, 90);

    private final AdminDashboardQueryRepository queryRepository;
    private final Clock clock;

    public AdminDashboardSnapshot getDashboard(int periodDays) {
        validatePeriodDays(periodDays);

        LocalDateTime asOf = LocalDateTime.now(clock);
        LocalDateTime todayStart = asOf.toLocalDate().atStartOfDay();
        LocalDateTime currentStart = todayStart.minusDays(periodDays);
        LocalDateTime comparisonStart = currentStart.minusDays(periodDays);

        DashboardPeriod currentPeriod = new DashboardPeriod(currentStart, todayStart);
        DashboardPeriod comparisonPeriod = new DashboardPeriod(comparisonStart, currentStart);
        LocalDate today = todayStart.toLocalDate();
        LocalDate currentDate = currentStart.toLocalDate();
        LocalDate comparisonDate = comparisonStart.toLocalDate();

        CollectionState activityState = queryRepository.findCollectionState("USER_ACTIVITY");
        CollectionState lifecycleState = queryRepository.findCollectionState("USER_LIFECYCLE");
        CollectionState aiState = queryRepository.findCollectionState("AI_USAGE");

        long todaySignups = queryRepository.countLifecycleEvents("SIGNUP", todayStart, asOf);
        var todaySummary = new AdminDashboardSnapshot.Today(
                todaySignups,
                queryRepository.countActiveUsers(today, today.plusDays(1)),
                queryRepository.countObservationCreated(today, today.plusDays(1))
        );

        long signups = queryRepository.countLifecycleEvents("SIGNUP", currentStart, todayStart);
        long previousSignups = queryRepository.countLifecycleEvents("SIGNUP", comparisonStart, currentStart);
        long signupWriters = queryRepository.countSignupUsersWithObservation(currentStart, todayStart);
        var cards = new AdminDashboardSnapshot.Cards(
                signups,
                percentChange(signups, previousSignups),
                percent(signupWriters, signups),
                queryRepository.countLifecycleEvents("WITHDRAWAL", currentStart, todayStart),
                queryRepository.countUnusedWithdrawals(currentStart, todayStart),
                availability(lifecycleState, comparisonDate, today.minusDays(1))
        );

        var currentDaily = dailySeries(currentDate, periodDays, activityState);
        var comparisonDaily = dailySeries(comparisonDate, periodDays, activityState);
        LocalDate recentStart = today.minusDays(7);
        LocalDate previousRecentStart = today.minusDays(14);
        long previousRecentUsers = queryRepository.countActiveUsers(previousRecentStart, recentStart);
        Double revisitRate = isCovered(activityState, previousRecentStart)
                ? percent(queryRepository.countReturningUsers(
                        previousRecentStart, recentStart, recentStart, today
                ), previousRecentUsers)
                : null;
        var dau = new AdminDashboardSnapshot.Dau(
                currentDaily,
                comparisonDaily,
                queryRepository.countActiveUsers(currentDate, today),
                revisitRate,
                availability(activityState, comparisonDate, today.minusDays(1))
        );

        FeatureUsageCounts usageCounts = defaultFeatureUsage(
                queryRepository.getFeatureUsage(currentDate, today)
        );
        var featureUsage = new AdminDashboardSnapshot.FeatureUsage(
                segment(usageCounts.observationOnly(), usageCounts.total()),
                segment(usageCounts.observationAndCommunity(), usageCounts.total()),
                segment(usageCounts.communityOnly(), usageCounts.total()),
                segment(usageCounts.browseOnly(), usageCounts.total()),
                availability(activityState, currentDate, today.minusDays(1))
        );

        GenerationCounts generationCounts = defaultGenerationCounts(
                queryRepository.getGenerationCounts(currentDate, today)
        );
        GenerationCounts yesterdayCounts = defaultGenerationCounts(
                queryRepository.getGenerationCounts(today.minusDays(1), today)
        );
        GenerationCounts previousSevenCounts = defaultGenerationCounts(
                queryRepository.getGenerationCounts(today.minusDays(8), today.minusDays(1))
        );
        LocalDate warningBaselineStart = today.minusDays(8);
        boolean warningDataAvailable = isCovered(activityState, warningBaselineStart);
        Double yesterdayRate = warningDataAvailable
                ? percent(yesterdayCounts.regenerated(), yesterdayCounts.total())
                : null;
        Double previousSevenRate = warningDataAvailable
                ? percent(previousSevenCounts.regenerated(), previousSevenCounts.total())
                : null;
        boolean regenerationWarning = warningDataAvailable
                && yesterdayCounts.total() >= 10
                && yesterdayRate != null
                && previousSevenRate != null
                && yesterdayRate - previousSevenRate >= 10.0;
        LocalDate regenerationRequestedFrom = currentDate.isBefore(warningBaselineStart)
                ? currentDate
                : warningBaselineStart;
        var regeneration = new AdminDashboardSnapshot.Regeneration(
                generationCounts.created(),
                generationCounts.regenerated(),
                percent(generationCounts.regenerated(), generationCounts.total()),
                new AdminDashboardSnapshot.RegenerationWarning(
                        yesterdayRate,
                        previousSevenRate,
                        regenerationWarning
                ),
                availability(activityState, regenerationRequestedFrom, today.minusDays(1))
        );

        AiUsageTotals aiTotals = defaultAiUsage(queryRepository.getAiUsage(currentStart, todayStart));
        var aiUsage = new AdminDashboardSnapshot.AiUsage(
                aiTotals.calls(),
                aiTotals.successes(),
                aiTotals.failures(),
                aiTotals.inputTokens(),
                aiTotals.outputTokens(),
                aiTotals.totalTokens(),
                aiTotals.averageDurationMs(),
                availability(aiState, currentDate, today.minusDays(1))
        );

        List<AdminDashboardSnapshot.RecentPost> recentPosts = safeList(queryRepository.findRecentPosts(5))
                .stream()
                .map(post -> new AdminDashboardSnapshot.RecentPost(
                        post.postId(), post.category(), post.title(), post.commentCount(), post.createdAt()
                ))
                .toList();

        boolean degraded = isDegraded(activityState) || isDegraded(lifecycleState) || isDegraded(aiState);

        return new AdminDashboardSnapshot(
                asOf,
                todayStart,
                periodDays,
                currentPeriod,
                comparisonPeriod,
                todaySummary,
                cards,
                dau,
                featureUsage,
                regeneration,
                aiUsage,
                recentPosts,
                new AdminDashboardSnapshot.DataQuality(degraded)
        );
    }

    private List<AdminDashboardSnapshot.DailyPoint> dailySeries(
            LocalDate start,
            int days,
            CollectionState state
    ) {
        LocalDate end = start.plusDays(days);
        Map<LocalDate, Long> counts = new HashMap<>();
        for (var dailyCount : safeList(queryRepository.findDailyActiveUsers(start, end))) {
            counts.put(dailyCount.date(), dailyCount.count());
        }

        return IntStream.range(0, days)
                .mapToObj(offset -> start.plusDays(offset))
                .map(date -> new AdminDashboardSnapshot.DailyPoint(
                        date,
                        isCovered(state, date) ? counts.getOrDefault(date, 0L) : null
                ))
                .toList();
    }

    private Availability availability(CollectionState state, LocalDate requestedFrom, LocalDate requestedTo) {
        if (state == null || state.availableFrom() == null || state.availableFrom().isAfter(requestedTo)) {
            return new Availability(AvailabilityStatus.COLLECTING, null, null, isDegraded(state));
        }

        LocalDate coveredFrom = state.availableFrom().isAfter(requestedFrom)
                ? state.availableFrom()
                : requestedFrom;
        AvailabilityStatus status = coveredFrom.isAfter(requestedFrom)
                ? AvailabilityStatus.PARTIAL
                : AvailabilityStatus.AVAILABLE;
        return new Availability(status, coveredFrom, requestedTo, state.gapDetected());
    }

    private boolean isCovered(CollectionState state, LocalDate date) {
        return state != null && state.availableFrom() != null && !date.isBefore(state.availableFrom());
    }

    private boolean isDegraded(CollectionState state) {
        return state != null && state.gapDetected();
    }

    private AdminDashboardSnapshot.UsageSegment segment(long count, long total) {
        return new AdminDashboardSnapshot.UsageSegment(count, percent(count, total));
    }

    private Double percent(long numerator, long denominator) {
        if (denominator == 0) {
            return null;
        }
        return numerator * 100.0 / denominator;
    }

    private Double percentChange(long current, long previous) {
        if (previous == 0) {
            return null;
        }
        return (current - previous) * 100.0 / previous;
    }

    private FeatureUsageCounts defaultFeatureUsage(FeatureUsageCounts counts) {
        return counts == null ? new FeatureUsageCounts(0, 0, 0, 0) : counts;
    }

    private GenerationCounts defaultGenerationCounts(GenerationCounts counts) {
        return counts == null ? new GenerationCounts(0, 0) : counts;
    }

    private AiUsageTotals defaultAiUsage(AiUsageTotals totals) {
        return totals == null ? new AiUsageTotals(0, 0, 0, 0, 0, 0, null) : totals;
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private void validatePeriodDays(int periodDays) {
        if (!SUPPORTED_PERIOD_DAYS.contains(periodDays)) {
            throw new IllegalArgumentException("periodDays must be one of 7, 30, 90");
        }
    }
}
